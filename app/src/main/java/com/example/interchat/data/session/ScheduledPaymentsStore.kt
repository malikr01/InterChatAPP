package com.example.interchat.data.session

//bu kısımda ekranlar burayı dinler şuan için mock store değiştiğinde ekran kendini yeniler

import com.example.interchat.domain.finance.ScheduledPayment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

/**
 * Planlı ödemeler için per-user store (kalıcı – app prosesi boyunca).
 * Kullanıcı değişince seed eder ama geri dönünce resetlemez.
 */
object ScheduledPaymentsStore {

    private val cache: MutableMap<String, List<ScheduledPayment>> = mutableMapOf()

    private val _items = MutableStateFlow<List<ScheduledPayment>>(emptyList())
    val items: StateFlow<List<ScheduledPayment>> = _items

    private var currentUserId: String? = null

    fun onUserChanged(uid: String?) {
        if (uid == currentUserId) return
        currentUserId = uid

        if (uid == null) {
            _items.value = emptyList()
            return
        }

        val existing = cache[uid]
        if (existing != null) {
            _items.value = existing
        } else {
            val seeded = defaultSeed(uid)
            cache[uid] = seeded
            _items.value = seeded
        }
    }

    /** Yeni planlı ödeme ekler ve sayaç artsın diye extras’a bir işlem düşer. */
    fun addMock() {
        val uid = currentUserId ?: return
        val newItem = ScheduledPayment(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Yeni Plan",
            dayOfMonth = 25,
            amount = 123.0
        )
        val updated = (cache[uid] ?: emptyList()) + newItem
        cache[uid] = updated
        _items.value = updated

        // ⬇ İşlem sayısını da arttır (canlı)
        TransactionExtrasStore.addPlannedPaymentAsTx(newItem.title, newItem.amount)
    }

    private fun defaultSeed(uid: String): List<ScheduledPayment> =
        when (uid) {
            "u1" -> listOf(
                ScheduledPayment(
                    id = "sp1",
                    userId = "u1",
                    title = "Elektrik",
                    dayOfMonth = 10,
                    amount = 400.0
                ),
                ScheduledPayment(
                    id = "sp2",
                    userId = "u1",
                    title = "Su",
                    dayOfMonth = 15,
                    amount = 180.0
                ),
            )
            "u2" -> listOf(
                ScheduledPayment(
                    id = "sp3",
                    userId = "u2",
                    title = "İnternet",
                    dayOfMonth = 20,
                    amount = 250.0
                ),
            )
            else -> emptyList()
        }
}