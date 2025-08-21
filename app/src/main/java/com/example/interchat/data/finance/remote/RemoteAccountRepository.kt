package com.example.interchat.data.finance.remote

// Accountu çağırır JSON u Domain Accounta çevirip döner

import com.example.interchat.data.finance.mock.MockAccountRepository
import com.example.interchat.data.net.ApiClient
import com.example.interchat.data.net.ApiRoutes
import com.example.interchat.data.session.AccountsStore
import com.example.interchat.domain.R
import com.example.interchat.domain.finance.Account
import com.example.interchat.domain.finance.AccountRepository

class RemoteAccountRepository(
    private val api: ApiClient,
    private val fallback: AccountRepository = MockAccountRepository()
) : AccountRepository {

    override suspend fun getAccounts(userId: String): List<Account> {
        val res = api.get(path = ApiRoutes.ACCOUNTS, query = mapOf("userId" to userId))
        return when (res) {
            is R.Ok -> {
                // TODO: JSON parse → List<Account>
                val cached = AccountsStore.items.value
                if (cached.isNotEmpty()) cached else fallback.getAccounts(userId)
            }
            is R.Err -> fallback.getAccounts(userId)
        }
    }
}
