package com.example.interchat.data.finance.mock

import com.example.interchat.data.net.ApiClient
import com.example.interchat.data.net.ApiRoutes
import com.example.interchat.data.session.AccountsStore
import com.example.interchat.data.session.TransactionExtrasStore
import com.example.interchat.domain.R
import com.example.interchat.domain.finance.TransferRepository

class MockTransferRepository(
    private val api: ApiClient? = null
) : TransferRepository {

    override suspend fun transfer(
        userId: String,
        fromAccountId: String,
        toIban: String,
        title: String,
        amount: Double
    ): Boolean {
        require(amount > 0) { "Tutar > 0 olmalı" }

        // (opsiyonel) POST simülasyonu
        val res = api?.post(
            path = ApiRoutes.TRANSFER,
            bodyJson = """{"userId":"$userId","fromAccountId":"$fromAccountId","toIban":"$toIban","title":"$title","amount":$amount}"""
        )
        if (res is R.Err) throw IllegalStateException("Transfer API başarısız: ${res.msg}")

        AccountsStore.applyDelta(fromAccountId, -amount)              // bakiye düş
        TransactionExtrasStore.addManualTransfer(title, amount)       // işlem sayısı +1
        return true
    }
}
