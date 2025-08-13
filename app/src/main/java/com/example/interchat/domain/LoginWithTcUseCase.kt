package com.example.interchat.domain

class LoginWithTcUseCase(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(tc: String, pass: String) = repo.loginWithTc(tc, pass)
}
