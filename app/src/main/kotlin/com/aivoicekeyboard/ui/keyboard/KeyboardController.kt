package com.aivoicekeyboard.ui.keyboard

import android.content.Context
import com.aivoicekeyboard.data.db.AppTriggerDao
import com.aivoicekeyboard.data.db.CustomModeDao
import com.aivoicekeyboard.data.local.ApiKeyManager
import com.aivoicekeyboard.engine.SttEngine
import com.aivoicekeyboard.engine.TextProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class KeyboardController(
    private val sttEngine: SttEngine,
    private val textProcessor: TextProcessor,
    private val apiKeyManager: ApiKeyManager,
    private val modeDao: CustomModeDao,
    private val triggerDao: AppTriggerDao,
    private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow<KeyboardState>(KeyboardState.Idle)
    val state: StateFlow<KeyboardState> = _state.asStateFlow()

    private val _revertSignal = MutableSharedFlow<LastInsertion?>()
    val revertSignal: SharedFlow<LastInsertion?> = _revertSignal.asSharedFlow()

    private var currentModeId: String? = null
    private var lastInsertion: LastInsertion? = null

    fun updateModeForPackage(packageName: String?) {
        if (packageName == null) return
        scope.launch {
            val trigger = triggerDao.getAllTriggers().first()
                .firstOrNull { it.packageName == packageName }
            currentModeId = trigger?.modeId
        }
    }

    fun startRecording() {
        if (!apiKeyManager.hasApiKey()) {
            _state.value = KeyboardState.Error("API key not set. Open Settings to add your key.")
            return
        }
        _state.value = KeyboardState.Recording
        scope.launch {
            val ready = sttEngine.ensureInitialized()
            if (!ready) {
                _state.value = KeyboardState.Error("Speech model not ready. Download it in Settings.")
                return@launch
            }
            sttEngine.startRecording { rawText ->
                if (rawText.isNullOrBlank()) {
                    _state.value = KeyboardState.Idle
                    return@startRecording
                }
                processRecording(rawText)
            }
        }
    }

    private fun processRecording(rawText: String) {
        _state.value = KeyboardState.Processing
        scope.launch {
            val result = textProcessor.process(rawText, currentModeId)
            result.onSuccess { processed ->
                lastInsertion = LastInsertion(
                    rawText = rawText,
                    processedCharCount = processed.length
                )
                _state.value = KeyboardState.Success(processed)
            }.onFailure { error ->
                _state.value = KeyboardState.Error(
                    message = error.message ?: "Processing failed",
                    fallbackText = rawText
                )
            }
        }
    }

    fun requestRevert() {
        val insertion = lastInsertion
        if (insertion != null) {
            scope.launch { _revertSignal.emit(insertion) }
            lastInsertion = null
        }
    }

    fun cancelRecording() {
        sttEngine.stopRecording()
        if (_state.value is KeyboardState.Recording) {
            _state.value = KeyboardState.Idle
        }
    }

    fun resetToIdle() {
        _state.value = KeyboardState.Idle
    }

    fun release() {
        sttEngine.release()
    }
}
