package com.aivoicekeyboard.ui.settings.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aivoicekeyboard.api.OpenRouterClient
import com.aivoicekeyboard.api.dto.ModelInfo
import com.aivoicekeyboard.data.db.AppTriggerDao
import com.aivoicekeyboard.data.db.CustomModeDao
import com.aivoicekeyboard.data.local.ApiKeyManager
import com.aivoicekeyboard.data.model.AppTrigger
import com.aivoicekeyboard.data.model.CustomMode
import com.aivoicekeyboard.util.ModelManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val modeDao: CustomModeDao,
    private val triggerDao: AppTriggerDao,
    private val apiKeyManager: ApiKeyManager,
    private val openRouterClient: OpenRouterClient,
    private val modelManager: ModelManager
) : AndroidViewModel(application) {

    val modes: StateFlow<List<CustomMode>> = modeDao.getAllModes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val triggers: StateFlow<List<AppTrigger>> = triggerDao.getAllTriggers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _apiKeyState = MutableStateFlow<ApiKeyState>(ApiKeyState.Unknown)
    val apiKeyState: StateFlow<ApiKeyState> = _apiKeyState.asStateFlow()

    private val _freeModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val freeModels: StateFlow<List<ModelInfo>> = _freeModels.asStateFlow()

    private val _modelStatus = MutableStateFlow<ModelStatus>(ModelStatus.Unknown)
    val modelStatus: StateFlow<ModelStatus> = _modelStatus.asStateFlow()

    private val _saveResult = MutableStateFlow<String?>(null)
    val saveResult: StateFlow<String?> = _saveResult.asStateFlow()

    init {
        checkApiKey()
        checkModelStatus()
    }

    fun saveApiKey(key: String) {
        apiKeyManager.saveApiKey(key)
        checkApiKey()
        loadFreeModels()
    }

    fun checkApiKey() {
        _apiKeyState.value = if (apiKeyManager.hasApiKey()) ApiKeyState.Valid else ApiKeyState.Missing
    }

    fun validateApiKey() {
        viewModelScope.launch {
            _apiKeyState.value = ApiKeyState.Checking
            val result = openRouterClient.getFreeModels()
            _apiKeyState.value = if (result.isSuccess) ApiKeyState.Valid
            else ApiKeyState.Invalid(result.exceptionOrNull()?.message ?: "Validation failed")
        }
    }

    fun loadFreeModels() {
        viewModelScope.launch {
            val result = openRouterClient.getFreeModels()
            result.onSuccess { _freeModels.value = it }
        }
    }

    fun saveMode(mode: CustomMode) {
        viewModelScope.launch {
            if (mode.isDefault) modeDao.clearAllDefaults()
            val existing = modeDao.getModeById(mode.id)
            if (existing != null) modeDao.update(mode) else modeDao.insert(mode)
            _saveResult.value = "Mode saved"
        }
    }

    fun deleteMode(mode: CustomMode) { viewModelScope.launch { modeDao.delete(mode) } }

    fun saveTrigger(packageName: String, modeId: String) {
        viewModelScope.launch { triggerDao.insert(AppTrigger(packageName = packageName, modeId = modeId)) }
    }

    fun deleteTrigger(trigger: AppTrigger) { viewModelScope.launch { triggerDao.delete(trigger) } }

    fun checkModelStatus() {
        _modelStatus.value = if (modelManager.isModelAvailable()) ModelStatus.Ready else ModelStatus.NotDownloaded
    }

    fun downloadModel(onProgress: (Float) -> Unit, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _modelStatus.value = ModelStatus.Downloading
            val success = modelManager.downloadModel { progress -> onProgress(progress) }
            _modelStatus.value = if (success) ModelStatus.Ready else ModelStatus.Error("Download failed")
            onComplete(success)
        }
    }

    fun clearSaveResult() { _saveResult.value = null }

    sealed class ApiKeyState {
        data object Unknown : ApiKeyState()
        data object Missing : ApiKeyState()
        data object Checking : ApiKeyState()
        data object Valid : ApiKeyState()
        data class Invalid(val reason: String) : ApiKeyState()
    }

    sealed class ModelStatus {
        data object Unknown : ModelStatus()
        data object NotDownloaded : ModelStatus()
        data object Downloading : ModelStatus()
        data object Ready : ModelStatus()
        data class Error(val message: String) : ModelStatus()
    }
}
