package com.example.interchat.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interchat.data.finance.mock.MockAccountRepository
import com.example.interchat.data.session.AccountsStore
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.Account
import com.example.interchat.domain.finance.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Hesap özetleri VM:
 * - AccountsStore'u veri kaynağı olarak kullanır (transfer sonrası canlı güncellenir)
 * - İlk kez kullanıcı için repo'dan seed eder
 */
class AccountsViewModel(
    private val repo: AccountRepository = MockAccountRepository()
) : ViewModel() {

    private val _loading  = MutableStateFlow(false)
    private val _error    = MutableStateFlow<String?>(null)
    private val _userId   = MutableStateFlow<String?>(null)

    val accounts: StateFlow<List<Account>> = AccountsStore.items
    val loading:  StateFlow<Boolean>       = _loading
    val error:    StateFlow<String?>       = _error
    val userId:   StateFlow<String?>       = _userId

    init {
        viewModelScope.launch {
            UserSession.userId.collect { uid ->
                _userId.value = uid
                AccountsStore.onUserChanged(uid)
                seedIfNeeded(uid)
            }
        }
    }

    fun refresh() = viewModelScope.launch { seedIfNeeded(_userId.value) }

    private suspend fun seedIfNeeded(uid: String?) {
        val id = uid ?: return
        try {
            _loading.value = true
            _error.value = null
            val list = repo.getAccounts(id)
            AccountsStore.seedIfEmpty(id, list)
        } catch (t: Throwable) {
            _error.value = t.message
        } finally {
            _loading.value = false
        }
    }
}
