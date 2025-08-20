package com.example.interchat.data.finance.mock

import com.example.interchat.data.session.AccountsStore
import com.example.interchat.data.session.TransactionExtrasStore
import com.example.interchat.domain.finance.TransferRepository

/**
 * Mock transfer:
 * - from hesabının bakiyesini düşer
 * - extras'a bir Transfer işlemi ekler (işlem sayısı artar)
 */
class MockTransferRepository : TransferRepository {
    override suspend fun transfer(
        userId: String,
        fromAccountId: String,
        toIban: String,
        title: String,
        amount: Double
    ): Boolean {
        require(amount > 0) { "Tutar > 0 olmalı" }
        // 1) bakiye düş
        AccountsStore.applyDelta(fromAccountId, -amount)
        // 2) işlem sayısını arttır (extras)
        TransactionExtrasStore.addManualTransfer(title.ifBlank { "Transfer" }, amount)
        return true
    }
}
