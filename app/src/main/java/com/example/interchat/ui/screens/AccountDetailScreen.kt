package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

private val moneyTr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("tr","TR"))
private fun Double.tl(): String = moneyTr.format(this)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailScreen(accountId: String, onBack: () -> Unit) {
    val vm: AccountsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val accounts = vm.accounts.collectAsState().value
    val acc = accounts.find { it.id == accountId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(acc?.name ?: "Hesap Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { p ->
        Column(
            modifier = Modifier
                .padding(p)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (acc == null) {
                Text("Hesap bulunamadı veya bağlı değilsiniz.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                return@Column
            }
            Text("Hesap Adı", style = MaterialTheme.typography.labelMedium)
            Text(acc.name, fontWeight = FontWeight.SemiBold)
            Divider()
            Text("IBAN", style = MaterialTheme.typography.labelMedium)
            Text(acc.iban)
            Divider()
            Text("Bakiye", style = MaterialTheme.typography.labelMedium)
            Text(acc.balance.tl(), fontWeight = FontWeight.SemiBold)
        }
    }
}
