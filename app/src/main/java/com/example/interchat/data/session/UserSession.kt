package com.example.interchat.data.session

//bu kısımda uygulamaya hangi kullanıcı bağlı

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object UserSession {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    fun setUserId(id: String?) { _userId.value = id }
    fun requireUserId(): String = _userId.value ?: "u1" // mock default
}