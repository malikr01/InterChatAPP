package com.example.interchat.domain.finance

import java.time.LocalDate

/* ---- Tipler ---- */
enum class TxType { Transfer, Bill, TopUp }

/* ---- Modeller ---- */
data class Account(
    val id: String,
    val name: String,
    val iban: String,
    val balance: Double,
)

data class Transaction(
    val id: String,
    val userId: String,
    val accountId: String,
    val title: String,
    val date: LocalDate,
    val amount: Double,   // giden:-, gelen:+
    val type: TxType
)

data class ScheduledPayment(
    val id: String,
    val userId: String,
    val title: String,
    val dayOfMonth: Int,
    val amount: Double
)

/* ---- Repository sözleşmeleri ---- */
interface AccountRepository {
    suspend fun getAccounts(userId: String): List<Account>
}

interface TransactionRepository {
    suspend fun getTransactions(userId: String): List<Transaction>
}

/** Hesaplar arası transfer sözleşmesi */
interface TransferRepository {
    suspend fun transfer(
        userId: String,
        fromAccountId: String,
        toIban: String,
        title: String,
        amount: Double
    ): Boolean
}
