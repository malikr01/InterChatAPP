package com.example.interchat.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/* ---- Chat Completions modelleri ---- */
data class ChatMessage(
    val role: String,   // "user" | "assistant" | "system"
    val content: String
)

data class ChatRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>,
    val temperature: Double = 0.7
)

data class ChatChoice(val index: Int, val message: ChatMessage)
data class ChatResponse(val id: String, val choices: List<ChatChoice>)

/* ---- Retrofit servisi ---- */
interface OpenAIService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chat(@Body body: ChatRequest): ChatResponse

    companion object {
        fun create(apiKey: String): OpenAIService {
            val authInterceptor = Interceptor { chain ->
                val newReq = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(newReq)
            }

            val log = HttpLoggingInterceptor().apply {
                // İstersen BODY yapıp Logcat'te istek/yanıtı görebilirsin
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(log)
                .build()

            // 🔧 EN ÖNEMLİ KISIM: KotlinJsonAdapterFactory
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(client)
                .build()

            return retrofit.create(OpenAIService::class.java)
        }
    }
}
