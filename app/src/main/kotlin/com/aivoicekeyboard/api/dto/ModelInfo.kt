package com.aivoicekeyboard.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelInfo(
    val id: String,
    val name: String,
    val description: String = "",
    @Json(name = "pricing") val pricing: PricingInfo? = null
)

@JsonClass(generateAdapter = true)
data class PricingInfo(
    val prompt: String = "0",
    val completion: String = "0"
)

@JsonClass(generateAdapter = true)
data class ChatRequest(
    val model: String,
    val messages: List<MessageDto>,
    val temperature: Float = 0.7,
    @Json(name = "max_tokens") val maxTokens: Int = 256
)

@JsonClass(generateAdapter = true)
data class MessageDto(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    val choices: List<ChoiceDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChoiceDto(
    val message: MessageDto? = null
)

@JsonClass(generateAdapter = true)
data class ModelsResponse(
    val data: List<ModelInfo> = emptyList()
)
