package com.aivoicekeyboard.api

import com.aivoicekeyboard.api.dto.*
import com.aivoicekeyboard.data.local.ApiKeyManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface OpenRouterApi {
    @GET("models")
    suspend fun getModels(): ModelsResponse

    @POST("chat/completions")
    @Headers("HTTP-Referer: https://aivoicekeyboard.app", "X-Title: AI Voice Keyboard")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse
}

class OpenRouterClient(private val apiKeyManager: ApiKeyManager) {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val key = apiKeyManager.getApiKey() ?: ""
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $key")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    private val api: OpenRouterApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(OpenRouterApi::class.java)
    }

    suspend fun getFreeModels(): Result<List<ModelInfo>> = runCatching {
        val response = api.getModels()
        response.data.filter {
            it.pricing?.prompt == "0" || it.pricing?.prompt.isNullOrEmpty()
        }
    }

    suspend fun processText(
        model: String,
        systemPrompt: String,
        userText: String,
        temperature: Float = 0.7f,
        maxTokens: Int = 256
    ): Result<String> = runCatching {
        val request = ChatRequest(
            model = model,
            messages = listOf(
                MessageDto(role = "system", content = systemPrompt),
                MessageDto(role = "user", content = userText)
            ),
            temperature = temperature,
            maxTokens = maxTokens
        )
        val response = api.chatCompletion(request)
        response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty response from model")
    }
}
