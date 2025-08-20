package com.example.interchat.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interchat.data.finance.mock.MockTransactionRepository
import com.example.interchat.data.session.TransactionExtrasStore
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.Transaction
import com.example.interchat.domain.finance.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * İşlem geçmişi VM
 * - repo'dan gelen "base" işlemler + TransactionExtrasStore'dan gelen "ek" işlemler = items
 * - userId değişince hem repo yüklenir hem extras store ayarlanır
 */
class TransactionsViewModel(
    private val repo: TransactionRepository = MockTransactionRepository()
) : ViewModel() {

    private val _base    = MutableStateFlow<List<Transaction>>(emptyList())
    private val _items   = MutableStateFlow<List<Transaction>>(emptyList())
    private val _loading = MutableStateFlow(false)
    private val _error   = MutableStateFlow<String?>(null)
    private val _userId  = MutableStateFlow<String?>(null)

    val items:   StateFlow<List<Transaction>> = _items
    val loading: StateFlow<Boolean>           = _loading
    val error:   StateFlow<String?>           = _error
    val userId:  StateFlow<String?>           = _userId

    init {
        // userId akışını dinle
        viewModelScope.launch {
            UserSession.userId.collect { uid ->
                _userId.value = uid
                TransactionExtrasStore.onUserChanged(uid) // ⬅ extras store'u yeni kullanıcıya geçir
                load()
            }
        }
        // extras değişince toplamı güncelle
        viewModelScope.launch {
            TransactionExtrasStore.items.collect { extras ->
                _items.value = _base.value + extras
            }
        }
    }

    fun refresh() = viewModelScope.launch { load() }

    private suspend fun load() {
        val uid = _userId.value
        _loading.value = true
        _error.value = null
        try {
            _base.value = if (uid == null) emptyList() else repo.getTransactions(uid)
            // toplam = base + extras
            _items.value = _base.value + TransactionExtrasStore.items.value
        } catch (t: Throwable) {
            _error.value = t.message ?: "Bilinmeyen hata"
        } finally {
            _loading.value = false
        }
    }
}
