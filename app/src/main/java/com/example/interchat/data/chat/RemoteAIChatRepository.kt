package com.example.interchat.data.chat

import com.example.interchat.data.di.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSource

/**
 * Backend AI servisine bağlanan repository.
 * Sözleşme:
 *  - SSE (tercih edilir): GET /ai/stream?prompt=...
 *      Content-Type: text/event-stream, satırlar: data: <parça> ... data: [DONE]
 *  - Fallback REST: POST /ai/chat  body: {"prompt":"..."} -> 200 OK "metin" veya JSON
 *
 * headerProvider: gerekli auth/özel header'ları üretir (Authorization, X-API-Key, vs.)
 */
class RemoteAIChatRepository(
    private val baseUrl: String = AppConfig.AI_BASE_URL,
    private val headerProvider: () -> Map<String, String> = { emptyMap() },
    private val client: OkHttpClient = OkHttpClient()
) : ChatRepository {

    override fun send(prompt: String): Flow<ChatEvent> = callbackFlow {
        // 1) SSE dene
        val sseUrl = (baseUrl.trimEnd('/') + "/ai/stream")
            .toHttpUrlOrNull()
            ?.newBuilder()
            ?.addQueryParameter("prompt", prompt)
            ?.build()

        if (sseUrl == null) {
            trySend(ChatEvent.Error("Geçersiz AI_BASE_URL"))
            close(); return@callbackFlow
        }

        val sseReqBuilder = Request.Builder()
            .url(sseUrl)
            .header("Accept", "text/event-stream")

        headerProvider().forEach { (k, v) -> sseReqBuilder.header(k, v) }

        val sseResp = runCatching { client.newCall(sseReqBuilder.get().build()).execute() }.getOrNull()

        if (sseResp == null || !sseResp.isSuccessful) {
            sseResp?.close()

            // 2) REST fallback
            val media = "application/json; charset=utf-8".toMediaType()
            val body = """{"prompt":${escapeJson(prompt)}}""".toRequestBody(media)

            val restReqBuilder = Request.Builder()
                .url(baseUrl.trimEnd('/') + "/ai/chat")
                .post(body)

            headerProvider().forEach { (k, v) -> restReqBuilder.header(k, v) }

            runCatching { client.newCall(restReqBuilder.build()).execute() }
                .onSuccess { resp ->
                    resp.use {
                        if (!it.isSuccessful) {
                            trySend(ChatEvent.Error("HTTP ${it.code}"))
                        } else {
                            trySend(ChatEvent.Done(it.body?.string().orEmpty()))
                        }
                    }
                }
                .onFailure { t ->
                    trySend(ChatEvent.Error("Bağlantı hatası: ${t.message}"))
                }

            close(); return@callbackFlow
        }

        // 3) SSE'yi satır satır oku
        val source: BufferedSource = sseResp.body!!.source()
        val buffer = StringBuilder()
        try {
            while (!source.exhausted()) {
                val line = source.readUtf8Line() ?: break
                if (!line.startsWith("data:")) continue
                val payload = line.removePrefix("data:").trim()
                if (payload == "[DONE]") break
                trySend(ChatEvent.Delta(payload))
                buffer.append(payload)
            }
            trySend(ChatEvent.Done(buffer.toString()))
        } catch (t: Throwable) {
            trySend(ChatEvent.Error("SSE kesildi: ${t.message}"))
        } finally {
            sseResp.close()
            close()
        }
    }

    private fun escapeJson(s: String): String = buildString {
        append('"')
        s.forEach {
            when (it) {
                '\\' -> append("\\\\")
                '"'  -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(it)
            }
        }
        append('"')
    }
}
