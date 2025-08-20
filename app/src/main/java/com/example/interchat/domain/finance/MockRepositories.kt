package com.example.interchat.data.finance.mock

import com.example.interchat.domain.finance.*
import kotlinx.coroutines.delay
import java.time.LocalDate

class MockAccountRepository : AccountRepository {
    private val dataByUser = mapOf(
        "u1" to listOf(
            Account("a1", "Vadesiz TL", "TR84 0006 2001 2345 6789 0001", 12540.75),
            Account("a2", "Birikim TL", "TR84 0006 2001 2345 6789 0002", 4200.00),
        )
    )

    override suspend fun getAccounts(userId: String): List<Account> {
        delay(150)
        return dataByUser[userId].orEmpty()
    }
}

class MockTransactionRepository : TransactionRepository {
    private val dataByUser = mapOf(
        "u1" to listOf(
            Transaction("t1","u1","a1","Ali Yılmaz",        LocalDate.now().minusDays(1), -250.00, TxType.Transfer),
            Transaction("t2","u1","a1","Elektrik Faturası", LocalDate.now().minusDays(2), -180.00, TxType.Bill),
            Transaction("t3","u1","a1","TL Yükleme",        LocalDate.now().minusDays(3),  -50.00, TxType.TopUp),
            Transaction("t4","u1","a1","Ayşe Kaya",         LocalDate.now().minusDays(4), -720.00, TxType.Transfer),
            Transaction("t5","u1","a1","Su Faturası",       LocalDate.now().minusDays(5), -140.00, TxType.Bill),
            Transaction("t6","u1","a2","Maaş",              LocalDate.now().minusDays(6), 8250.00, TxType.Transfer),
        )
    )

    override suspend fun getTransactions(userId: String): List<Transaction> {
        delay(200)
        return dataByUser[userId].orEmpty()
    }
}
