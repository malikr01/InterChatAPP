package com.example.interchat.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interchat.data.finance.mock.MockTransferRepository
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.TransferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransferViewModel(
    private val repo: TransferRepository = MockTransferRepository()
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    private val _result  = MutableStateFlow<String?>(null)
    private val _error   = MutableStateFlow<String?>(null)

    val loading: StateFlow<Boolean> = _loading
    val result:  StateFlow<String?> = _result
    val error:   StateFlow<String?> = _error

    fun doTransfer(fromAccountId: String, toIban: String, title: String, amount: Double) {
        val uid = UserSession.userId.value ?: return
        viewModelScope.launch {
            _loading.value = true
            _result.value = null
            _error.value = null
            try {
                repo.transfer(uid, fromAccountId, toIban, title, amount)
                _result.value = "Transfer tamam."
            } catch (t: Throwable) {
                _error.value = t.message ?: "Bilinmeyen hata"
            } finally {
                _loading.value = false
            }
        }
    }
}
