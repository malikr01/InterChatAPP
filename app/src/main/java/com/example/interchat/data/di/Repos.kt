package com.example.interchat.data.di

//Bütün ekranlar veriyi buradan çağırır
//mock ise finance/mock altındakileri backend ise finance/remote daki verileri çeker.

import com.example.interchat.data.finance.mock.MockAccountRepository
import com.example.interchat.data.finance.mock.MockTransactionRepository
import com.example.interchat.data.finance.mock.MockTransferRepository
import com.example.interchat.data.finance.remote.RemoteAccountRepository
import com.example.interchat.data.finance.remote.RemoteTransactionRepository
import com.example.interchat.data.finance.remote.RemoteTransferRepository
import com.example.interchat.data.net.ApiClient
import com.example.interchat.data.net.DummyApiClient
import com.example.interchat.data.net.HttpApiClient
import com.example.interchat.domain.finance.AccountRepository
import com.example.interchat.domain.finance.TransactionRepository
import com.example.interchat.domain.finance.TransferRepository

// 💬 AI
import com.example.interchat.data.chat.ChatRepository
import com.example.interchat.data.chat.MockAIChatRepository
import com.example.interchat.data.chat.RemoteAIChatRepository

object Repos {

    // --- Finans repos seçimi (mevcut mantık korunuyor) ---
    private val useRemote: Boolean = AppConfig.API_BASE_URL.isNotBlank()

    val api: ApiClient =
        if (useRemote) HttpApiClient(AppConfig.API_BASE_URL)
        else DummyApiClient()

    val accountRepo: AccountRepository =
        if (useRemote) RemoteAccountRepository(api) else MockAccountRepository()

    val transactionRepo: TransactionRepository =
        if (useRemote) RemoteTransactionRepository(api) else MockTransactionRepository()

    val transferRepo: TransferRepository =
        if (useRemote) RemoteTransferRepository(api) else MockTransferRepository(api = api)

    // --- AI (Chat) repos seçimi ---
    private fun aiAuthHeaders(): Map<String, String> = when (AppConfig.AI_AUTH_MODE) {
        AppConfig.AIAuthMode.None -> emptyMap()
        AppConfig.AIAuthMode.Bearer -> {
            val token = AppConfig.AI_STATIC_BEARER.trim()
            if (token.isNotEmpty()) mapOf("Authorization" to "Bearer $token") else emptyMap()
        }
        AppConfig.AIAuthMode.ApiKey -> {
            val key = AppConfig.AI_API_KEY.trim()
            val header = AppConfig.AI_API_KEY_HEADER.trim().ifEmpty { "X-API-Key" }
            if (key.isNotEmpty()) mapOf(header to key) else emptyMap()
        }
    }

    val aiChatRepo: ChatRepository by lazy {
        when (AppConfig.AI_SOURCE) {
            AppConfig.AISource.Mock -> MockAIChatRepository()
            AppConfig.AISource.Backend -> RemoteAIChatRepository(
                baseUrl = AppConfig.AI_BASE_URL,
                headerProvider = ::aiAuthHeaders
            )
        }
    }
}
