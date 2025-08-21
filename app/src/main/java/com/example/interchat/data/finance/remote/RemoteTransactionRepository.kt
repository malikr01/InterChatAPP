package com.example.interchat.data.finance.remote

//Transaction ı çağırır gelen listeyi domain Transaction a çevirir

import com.example.interchat.data.finance.mock.MockTransactionRepository
import com.example.interchat.data.net.ApiClient
import com.example.interchat.data.net.ApiRoutes
import com.example.interchat.domain.R
import com.example.interchat.domain.finance.Transaction
import com.example.interchat.domain.finance.TransactionRepository

class RemoteTransactionRepository(
    private val api: ApiClient,
    private val fallback: TransactionRepository = MockTransactionRepository()
) : TransactionRepository {

    override suspend fun getTransactions(userId: String): List<Transaction> {
        val res = api.get(path = ApiRoutes.TRANSACTIONS, query = mapOf("userId" to userId))
        return when (res) {
            is R.Ok -> {
                // TODO: JSON parse → List<Transaction>
                fallback.getTransactions(userId)
            }
            is R.Err -> fallback.getTransactions(userId)
        }
    }
}
