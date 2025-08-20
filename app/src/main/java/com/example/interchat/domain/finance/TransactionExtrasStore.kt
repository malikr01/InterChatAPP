package com.example.interchat.data.session

import com.example.interchat.domain.finance.Transaction
import com.example.interchat.domain.finance.TxType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.util.UUID
import kotlin.math.abs

/**
 * Repo dışı ek işlemler (ör. planlı ödeme/manuel transfer) için hafif store.
 * - Kullanıcı bazlı cache
 * - ViewModel'ler canlı dinler (items)
 */
object TransactionExtrasStore {

    private val cache: MutableMap<String, List<Transaction>> = mutableMapOf()

    private val _items = MutableStateFlow<List<Transaction>>(emptyList())
    val items: StateFlow<List<Transaction>> = _items

    private var currentUserId: String? = null

    fun onUserChanged(uid: String?) {
        if (uid == currentUserId) return
        currentUserId = uid
        _items.value = if (uid == null) emptyList() else cache[uid].orEmpty()
    }

    /** Planlı ödeme eklendiğinde bir 'Bill' işlemi de düş. */
    fun addPlannedPaymentAsTx(title: String, amount: Double) =
        addExtraTx("Planlı: $title", -abs(amount), TxType.Bill)

    /** Hızlı transfer ekranından manuel işlem eklemek için (sayaç artsın). */
    fun addManualTransfer(title: String, amount: Double) =
        addExtraTx(if (title.isBlank()) "Transfer" else title, -abs(amount), TxType.Transfer)

    private fun addExtraTx(title: String, amount: Double, type: TxType) {
        val uid = currentUserId ?: return
        val newTx = Transaction(
            id = UUID.randomUUID().toString(),
            userId = uid,
            accountId = "a1", // mock
            title = title,
            date = LocalDate.now(),
            amount = amount,
            type = type
        )
        val updated = (cache[uid] ?: emptyList()) + newTx
        cache[uid] = updated
        _items.value = updated
    }

    fun clearForCurrent() {
        val uid = currentUserId ?: return
        cache.remove(uid)
        _items.value = emptyList()
    }
}
