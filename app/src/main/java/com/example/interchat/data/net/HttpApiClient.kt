package com.example.interchat.data.net

//HTTP kullanan gerçek istemci  KISACA Ekrana gelen istek > Repos > Remote/Repository> API client > Sunucu

import com.example.interchat.domain.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID

class HttpApiClient(
    private val baseUrl: String,
    private val tokenProvider: () -> String? = { null }
) : ApiClient {

    private val json = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()

    override suspend fun get(
        path: String,
        query: Map<String, String>,
        headers: Map<String, String>
    ): R<String> {
        val url = buildString {
            append(baseUrl).append(path)
            if (query.isNotEmpty()) append('?')
                .append(query.entries.joinToString("&") { "${it.key}=${it.value}" })
        }
        val req = Request.Builder().url(url).apply {
            headers.forEach { (k,v) -> addHeader(k,v) }
            tokenProvider()?.let { addHeader("Authorization", "Bearer $it") }
        }.get().build()
        return exec(req)
    }

    override suspend fun post(
        path: String,
        bodyJson: String,
        headers: Map<String, String>
    ): R<String> {
        val req = Request.Builder().url(baseUrl + path).apply {
            addHeader("Content-Type","application/json")
            addHeader("Idempotency-Key", UUID.randomUUID().toString())
            headers.forEach { (k,v) -> addHeader(k,v) }
            tokenProvider()?.let { addHeader("Authorization", "Bearer $it") }
        }.post(bodyJson.toRequestBody(json)).build()
        return exec(req)
    }

    override suspend fun put(path: String, bodyJson: String, headers: Map<String, String>) =
        R.Err("not_implemented")

    override suspend fun delete(path: String, headers: Map<String, String>) =
        R.Err("not_implemented")

    private fun exec(req: Request): R<String> = try {
        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            if (resp.isSuccessful) R.Ok(body) else R.Err("${resp.code}: $body")
        }
    } catch (t: Throwable) { R.Err(t.message ?: "network_error") }
}
