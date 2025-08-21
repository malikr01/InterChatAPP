package com.example.interchat.data.finance.remote

import com.example.interchat.data.net.ApiClient
import com.example.interchat.data.net.ApiRoutes
import com.example.interchat.data.session.AccountsStore
import com.example.interchat.data.session.TransactionExtrasStore
import com.example.interchat.domain.R
import com.example.interchat.domain.finance.TransferRepository
//account u günceller TransactionExtrasStore’a yeni satır ekler
/**
 * Remote transfer – backend hazır olduğunda gerçek response parse edilir.
 * Şimdilik POST atar, OK ise optimistic update uygular.
 */
class RemoteTransferRepository(
    private val api: ApiClient
) : TransferRepository {

    override suspend fun transfer(
        userId: String,
        fromAccountId: String,
        toIban: String,
        title: String,
        amount: Double
    ): Boolean {
        require(amount > 0) { "Tutar > 0 olmalı" }

        val body = """
            {
              "userId":"$userId",
              "fromAccountId":"$fromAccountId",
              "toIban":"$toIban",
              "title":"$title",
              "amount":$amount
            }
        """.trimIndent()

        return when (val res = api.post(ApiRoutes.TRANSFER, body)) {
            is R.Ok -> {
                // optimistic update
                AccountsStore.applyDelta(fromAccountId, -amount)
                TransactionExtrasStore.addManualTransfer(title.ifBlank { "Transfer" }, amount)
                true
            }
            is R.Err -> throw IllegalStateException("Transfer API hatası: ${res.msg}")
        }
    }
}
