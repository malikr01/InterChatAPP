package com.example.interchat.domain

interface AuthRepository {
    suspend fun register(tc: String, password: String): R<Unit>
    suspend fun loginWithTc(tc: String, password: String): R<User>
    suspend fun logout(): R<Unit>
    fun isLoggedIn(): Boolean
}
