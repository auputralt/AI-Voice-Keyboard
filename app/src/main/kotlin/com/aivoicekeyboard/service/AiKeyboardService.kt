package com.aivoicekeyboard.service

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.aivoicekeyboard.data.db.AppTriggerDao
import com.aivoicekeyboard.data.db.CustomModeDao
import com.aivoicekeyboard.data.local.ApiKeyManager
import com.aivoicekeyboard.engine.SttEngine
import com.aivoicekeyboard.engine.TextProcessor
import com.aivoicekeyboard.ui.keyboard.KeyboardComposeView
import com.aivoicekeyboard.ui.keyboard.KeyboardController
import com.aivoicekeyboard.ui.keyboard.KeyboardState
import com.aivoicekeyboard.ui.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AiKeyboardService : InputMethodService(), LifecycleOwner {

    @Inject lateinit var sttEngine: SttEngine
    @Inject lateinit var textProcessor: TextProcessor
    @Inject lateinit var apiKeyManager: ApiKeyManager
    @Inject lateinit var modeDao: CustomModeDao
    @Inject lateinit var triggerDao: AppTriggerDao

    private lateinit var lifecycleRegistry: LifecycleRegistry
    private lateinit var controller: KeyboardController
    private var stateJob: Job? = null

    override val lifecycle: Lifecycle get() = lifecycleRegistry

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        controller = KeyboardController(
            sttEngine = sttEngine,
            textProcessor = textProcessor,
            apiKeyManager = apiKeyManager,
            modeDao = modeDao,
            triggerDao = triggerDao,
            context = this
        )

        stateJob = lifecycleScope.launch {
            controller.state.collect { state ->
                when (state) {
                    is KeyboardState.Success -> {
                        val ic = currentInputConnection
                        if (ic != null) {
                            ic.beginBatchEdit()
                            ic.commitText(state.text, 1)
                            ic.endBatchEdit()
                        }
                        delay(400)
                        controller.resetToIdle()
                    }
                    is KeyboardState.Error -> {
                        if (state.fallbackText != null) {
                            val ic = currentInputConnection
                            if (ic != null) {
                                ic.beginBatchEdit()
                                ic.commitText(state.fallbackText, 1)
                                ic.endBatchEdit()
                            }
                        }
                        delay(2500)
                        if (controller.state.value is KeyboardState.Error) controller.resetToIdle()
                    }
                    else -> { }
                }
            }
        }

        lifecycleScope.launch {
            controller.revertSignal.collect { lastInsertion ->
                if (lastInsertion == null) return@collect
                val ic = currentInputConnection ?: return@collect
                ic.beginBatchEdit()
                ic.deleteSurroundingText(lastInsertion.processedCharCount, 0)
                ic.commitText(lastInsertion.rawText, 1)
                ic.endBatchEdit()
            }
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        handleInputStart(attribute)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        handleInputStart(info)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        controller.cancelRecording()
        super.onFinishInputView(finishingInput)
    }

    override fun onEvaluateInputViewShown(): Boolean = true

    private fun handleInputStart(info: EditorInfo?) {
        val inputType = info?.inputType ?: 0
        val variation = inputType and android.text.InputType.TYPE_MASK_VARIATION
        val isSensitive = variation == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                (inputType and android.text.InputType.TYPE_CLASS_NUMBER == android.text.InputType.TYPE_CLASS_NUMBER &&
                 variation == android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD)

        if (isSensitive) {
            controller.cancelRecording()
            return
        }

        controller.updateModeForPackage(info?.packageName?.toString())
    }

    override fun onCreateInputView(): View {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        return KeyboardComposeView(
            context = this,
            controller = controller,
            onSettingsClick = {
                val intent = Intent(this, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        )
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        stateJob?.cancel()
        controller.release()
        super.onDestroy()
    }
}
