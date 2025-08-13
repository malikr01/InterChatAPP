// app/src/main/java/com/example/interchat/data/MockAuthRepository.kt
package com.example.interchat.data

import com.example.interchat.domain.AuthRepository
import com.example.interchat.domain.R
import com.example.interchat.domain.User
import com.example.interchat.domain.isValidTc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class MockAuthRepository(
    private val store: CredentialsStore
) : AuthRepository {

    private var loggedIn = false
    private var currentUser: User? = null

    override suspend fun register(tc: String, password: String): R<Unit> {
        delay(300) // simulate network
        store.saveCredentials(tc, password) // ✅ burada save değil saveCredentials
        return R.Ok(Unit)
    }


    override suspend fun loginWithTc(tc: String, password: String): R<User> {
        delay(250)
        if (!tc.isValidTc()) return R.Err("TC 11 haneli olmalı ve 0 ile başlamamalı.")
        if (password.length < 4) return R.Err("Şifre en az 4 karakter olmalı.")

        val (savedTc, savedPass) = store.credentials.first()
        if (savedTc == null || savedPass == null) {
            return R.Err("Kayıtlı kullanıcı bulunamadı. Lütfen önce kayıt olun.")
        }
        if (tc != savedTc || password != savedPass) {
            return R.Err("TC veya şifre hatalı.")
        }

        val user = User(id = "u_${tc.takeLast(4)}", tc = tc)
        currentUser = user
        loggedIn = true
        return R.Ok(user)
    }

    override suspend fun logout(): R<Unit> {
        delay(150)
        loggedIn = false
        currentUser = null
        return R.Ok(Unit)
    }

    override fun isLoggedIn(): Boolean = loggedIn
}
