package com.example.interchat.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interchat.data.di.Repos
import com.example.interchat.data.session.TransactionExtrasStore
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TransactionsViewModel : ViewModel() {

    private val _loading = MutableStateFlow(false)
    private val _error   = MutableStateFlow<String?>(null)
    private val _userId  = MutableStateFlow<String?>(null)

    val loading: StateFlow<Boolean> = _loading
    val error:   StateFlow<String?> = _error
    val userId:  StateFlow<String?> = _userId

    private val _serverItems = MutableStateFlow<List<Transaction>>(emptyList())
    private val extras = TransactionExtrasStore.items

    /** extras + server birleşik liste (extras önce) */
    val items: StateFlow<List<Transaction>> =
        combine(extras, _serverItems) { ex, srv -> ex + srv }
            .map { list -> list.sortedByDescending { it.date } }
            .let { flow ->
                val out = MutableStateFlow(emptyList<Transaction>())
                viewModelScope.launch { flow.collect { out.value = it } }
                out
            }

    init {
        viewModelScope.launch {
            UserSession.userId.collect { uid ->
                _userId.value = uid
                TransactionExtrasStore.onUserChanged(uid)
                load()
            }
        }
    }

    fun refresh() = viewModelScope.launch { load() }

    private suspend fun load() {
        val uid = _userId.value ?: return
        try {
            _loading.value = true
            _error.value = null

            // ✅ Çoğu projede repo direkt List<Transaction> döner:
            val list: List<Transaction> = Repos.transactionRepo.getTransactions(uid)
            _serverItems.value = list

            // Eğer senin repo sayfalama döndürüyorsa şuna çevir:
            // val page = Repos.transactionRepo.getTransactions(uid)
            // _serverItems.value = page.items
        } catch (t: Throwable) {
            _error.value = t.message
        } finally {
            _loading.value = false
        }
    }
}
