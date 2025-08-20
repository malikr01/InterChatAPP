package com.example.interchat.data.session

import com.example.interchat.domain.finance.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Hesap listesi için per-user cache + akış.
 * - kullanıcı değişince onUserChanged(uid)
 * - seedIfEmpty(uid, initial) ilk kez doldurur (transfer ile değişen bakiyeleri ezmez)
 * - applyDelta(accountId, delta) bakiyeye etki eder (ör: -150.0)
 */
object AccountsStore {

    private val cache: MutableMap<String, List<Account>> = mutableMapOf()
    private var currentUserId: String? = null

    private val _items = MutableStateFlow<List<Account>>(emptyList())
    val items: StateFlow<List<Account>> = _items

    fun onUserChanged(uid: String?) {
        if (uid == currentUserId) return
        currentUserId = uid
        _items.value = if (uid == null) emptyList() else cache[uid].orEmpty()
    }

    fun seedIfEmpty(uid: String, initial: List<Account>) {
        if (cache.containsKey(uid)) {
            if (uid == currentUserId) _items.value = cache[uid].orEmpty()
            return
        }
        cache[uid] = initial
        if (uid == currentUserId) _items.value = initial
    }

    fun applyDelta(accountId: String, delta: Double) {
        val uid = currentUserId ?: return
        val list = cache[uid].orEmpty()
        val updated = list.map { acc ->
            if (acc.id == accountId) acc.copy(balance = acc.balance + delta) else acc
        }
        cache[uid] = updated
        _items.value = updated
    }
}
