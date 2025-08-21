package com.example.interchat.data.net

import com.example.interchat.domain.R

//Get/Post çağrılarını bu kısım yapar Api Ağ katmanı
interface ApiClient {
    suspend fun get(path: String, query: Map<String, String> = emptyMap(), headers: Map<String, String> = emptyMap()): R<String>
    suspend fun post(path: String, bodyJson: String, headers: Map<String, String> = mapOf("Content-Type" to "application/json")): R<String>
    suspend fun put(path: String, bodyJson: String, headers: Map<String, String> = mapOf("Content-Type" to "application/json")): R<String>
    suspend fun delete(path: String, headers: Map<String, String> = emptyMap()): R<String>
}

object ApiRoutes {
    const val ACCOUNTS     = "/accounts"
    const val TRANSACTIONS = "/transactions"
    const val TRANSFER     = "/transfer"
}
