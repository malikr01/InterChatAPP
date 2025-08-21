package com.example.interchat.data.session

//Hesap listesi ve bakiyeler

import com.example.interchat.domain.finance.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AccountsStore {
    private var currentUser: String? = null
    private val _items = MutableStateFlow<List<Account>>(emptyList())
    val items: StateFlow<List<Account>> = _items

    fun onUserChanged(userId: String?) {
        if (userId != currentUser) {
            currentUser = userId
            _items.value = emptyList()
        }
    }

    /** Repo’dan ilk kez doldururken kullan: doluysa ellemeyelim (optimistic update’i ezmemek için) */
    fun seedIfEmpty(userId: String, list: List<Account>) {
        if (currentUser == userId && _items.value.isEmpty()) {
            _items.value = list
        }
    }

    /** Zorunlu senaryoda sunucuyu “tek doğru kaynak” yapmak istersen: */
    fun setAll(userId: String, list: List<Account>) {
        if (currentUser == userId) _items.value = list
    }

    /** Transfer sonrası bakiyeyi anında güncelle (yeni liste atarak state’i tetikle) */
    fun applyDelta(accountId: String, delta: Double) {
        _items.value = _items.value.map { acc ->
            if (acc.id == accountId) acc.copy(balance = acc.balance + delta) else acc
        }
    }
}
