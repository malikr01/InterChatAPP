package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.*

private fun money(minor: Long, cur: Currency): String =
    NumberFormat.getCurrencyInstance(Locale("tr","TR")).apply { currency = cur }
        .format(minor / 100.0)

private val demoAccounts = listOf(
    Account("1","Vadesiz TL","TR12 1234 5678 9012 3456 78", Currency.getInstance("TRY"), 1_284_500),
    Account("2","Vadeli TL","TR90 0001 2345 6789 0000 11", Currency.getInstance("TRY"), 15_400_000)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(accountId: String, onBack: () -> Unit) {
    val acc = demoAccounts.find { it.id == accountId }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(acc?.title ?: "Hesap Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Geri") }
                }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            if (acc == null) { Text("Hesap bulunamadı."); return@Column }
            Text("IBAN: ${acc.iban}")
            Spacer(Modifier.height(8.dp))
            Text("Bakiye: ${money(acc.balanceMinor, acc.currency)}")
        }
    }
}
