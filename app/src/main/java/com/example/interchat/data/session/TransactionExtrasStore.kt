package com.example.interchat.data.session

//ek işlemler hesabın görüntülenmesi

import com.example.interchat.domain.finance.TxType
import com.example.interchat.domain.finance.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.util.UUID

object TransactionExtrasStore {
    private var currentUser: String? = null
    private val _items = MutableStateFlow<List<Transaction>>(emptyList())
    val items: StateFlow<List<Transaction>> = _items

    fun onUserChanged(userId: String?) {
        if (userId != currentUser) {
            currentUser = userId
            _items.value = emptyList()
        }
    }

    /** Hızlı/manuel transferi anında geçmişe düşür. */
    fun addManualTransfer(
        title: String,
        amount: Double,
        accountId: String? = null
    ) {
        val t = Transaction(
            id = UUID.randomUUID().toString(),
            userId = currentUser.orEmpty(),
            accountId = accountId.orEmpty(),
            title = title.ifBlank { "Transfer" },
            date = LocalDate.now(),
            amount = -kotlin.math.abs(amount),   // giden para negatif
            type = TxType.Transfer               // ✔ senin enum: Transfer/Bill/TopUp
        )
        _items.value = listOf(t) + _items.value
    }

    /** Planlı ödemeyi (ör. elektrik) geçmişe 'ödenmiş' olarak at. */
    fun addPlannedPaymentAsTx(
        title: String,
        amount: Double
    ) {
        val t = Transaction(
            id = UUID.randomUUID().toString(),
            userId = currentUser.orEmpty(),
            accountId = "",                      // istersen ilgili hesap id’sini de geçebilirsin
            title = title.ifBlank { "Planlı Ödeme" },
            date = LocalDate.now(),
            amount = -kotlin.math.abs(amount),   // ödeme → negatif
            type = TxType.Bill                   // ✔ “ödeme” / “fatura” olarak işaretle
        )
        _items.value = listOf(t) + _items.value
    }

    fun clear() { _items.value = emptyList() }
}
