package com.example.interchat.data.net

import com.example.interchat.domain.R
import kotlinx.coroutines.delay

class DummyApiClient : ApiClient {
    override suspend fun get(path: String, query: Map<String, String>, headers: Map<String, String>) = R.Ok("""{"ok":true,"path":"$path"}""").also { delay(120) }
    override suspend fun post(path: String, bodyJson: String, headers: Map<String, String>) = R.Ok("""{"ok":true,"path":"$path","echo":$bodyJson}""").also { delay(150) }
    override suspend fun put(path: String, bodyJson: String, headers: Map<String, String>) = R.Ok("""{"ok":true}""").also { delay(150) }
    override suspend fun delete(path: String, headers: Map<String, String>) = R.Ok("""{"ok":true}""").also { delay(100) }
}
