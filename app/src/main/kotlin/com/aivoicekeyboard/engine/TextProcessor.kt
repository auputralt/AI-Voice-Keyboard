package com.aivoicekeyboard.engine

import com.aivoicekeyboard.api.OpenRouterClient
import com.aivoicekeyboard.data.db.CustomModeDao
import com.aivoicekeyboard.data.model.CustomMode
import kotlinx.coroutines.flow.first

class TextProcessor(
    private val client: OpenRouterClient,
    private val modeDao: CustomModeDao
) {
    suspend fun process(rawText: String, modeId: String? = null): Result<String> {
        val mode = resolveMode(modeId)
        return client.processText(
            model = "openrouter/free",
            systemPrompt = mode.prompt,
            userText = rawText,
            temperature = mode.temperature,
            maxTokens = mode.maxTokens
        )
    }

    private suspend fun resolveMode(modeId: String?): CustomMode {
        if (modeId != null) {
            val mode = modeDao.getModeById(modeId)
            if (mode != null) return mode
        }
        val allModes = modeDao.getAllModes().first()
        return allModes.firstOrNull { it.isDefault } ?: allModes.firstOrNull()
            ?: CustomMode(
                id = "default",
                name = "Default",
                prompt = "Rewrite the following text with correct grammar and spelling. Return only the corrected text.",
                isDefault = true
            )
    }
}
