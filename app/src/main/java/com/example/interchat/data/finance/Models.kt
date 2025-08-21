package com.example.interchat.domain.finance

import java.time.LocalDate

enum class TxType { Transfer, Bill, TopUp }

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

//Backend JSON alan adları ile domain modeller (Account/Transaction) arasında dönüşüm backend ile alan adları değişirse
//değişecek ilk yer burası
data class ScheduledPayment(
    val id: String,
    val userId: String,
    val title: String,
    val dayOfMonth: Int,
    val amount: Double
)

/** Repo arayüzleri */
interface AccountRepository { suspend fun getAccounts(userId: String): List<Account> }
interface TransactionRepository { suspend fun getTransactions(userId: String): List<Transaction> }
interface TransferRepository {
    suspend fun transfer(
        userId: String,
        fromAccountId: String,
        toIban: String,
        title: String,
        amount: Double
    ): Boolean
}
