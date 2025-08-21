package com.example.interchat.data.finance.mock

import com.example.interchat.data.session.AccountsStore
import com.example.interchat.domain.finance.Account
import com.example.interchat.domain.finance.AccountRepository
import com.example.interchat.domain.finance.Transaction
import com.example.interchat.domain.finance.TransactionRepository
import com.example.interchat.domain.finance.TxType
import java.time.LocalDate

/** Hesaplar için basit mock */
class MockAccountRepository : AccountRepository {
    override suspend fun getAccounts(userId: String): List<Account> {
        // Store’da varsa aynen dön
        val cached = AccountsStore.items.value
        if (cached.isNotEmpty()) return cached

        // Seed verisi
        val seed = listOf(
            Account(id = "a1", name = "Vadesiz TL", iban = "TR00 0000 0000 0000 0001 0001",
                balance = 12_345.67),
            Account(id = "a2", name = "Birikim", iban = "TR00 0000 0000 0000 0001 0002",
                balance = 890.00),
        )
        AccountsStore.seedIfEmpty(userId, seed)
        return seed
    }
}

/** İşlemler için basit mock */
class MockTransactionRepository : TransactionRepository {
    override suspend fun getTransactions(userId: String): List<Transaction> {
        val today = LocalDate.now()
        return listOf(
            Transaction(
                id = "t1", userId = userId, accountId = "a1",
                title = "Elektrik Faturası", date = today.minusDays(2),
                amount = -450.0, type = TxType.Bill
            ),
            Transaction(
                id = "t2", userId = userId, accountId = "a1",
                title = "Maaş", date = today.minusDays(10),
                amount = 17_500.0, type = TxType.TopUp
            ),
            Transaction(
                id = "t3", userId = userId, accountId = "a1",
                title = "Kira", date = today.minusDays(12),
                amount = -5_000.0, type = TxType.Bill
            ),
        )
    }
}
