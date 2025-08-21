@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.interchat.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.Account
import java.text.NumberFormat
import java.util.Locale

/* ---------------- helpers ---------------- */

private val moneyTr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("tr","TR"))
private fun Double.tl(): String = moneyTr.format(this)

private fun String.maskIban(): String {
    val clean = replace(" ", "")
    if (clean.length < 12) return this
    val head = clean.take(6)
    val tail = clean.takeLast(4)
    return (head + "**************" + tail).chunked(4).joinToString(" ")
}

/* ---------------- status bar ---------------- */

@Composable
private fun AccountsStatusBar(
    userId: String?,
    totalBalance: Double,
    loading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(if (userId != null) "Bağlı: $userId" else "Bağlı değil") })
            AssistChip(onClick = {}, label = { Text("Toplam: ${totalBalance.tl()}") })
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = onRefresh, enabled = !loading) { Text("Yenile") }
        }
        if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (error != null) Text("Hata: $error", color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { UserSession.setUserId("u1") }) { Text("Bağlan (u1)") }
            OutlinedButton(onClick = { UserSession.setUserId(null) }) { Text("Bağlantıyı Kes") }
        }
        Divider()
    }
}

/* ---------------- account card ---------------- */

@Composable
private fun AccountCard(account: Account, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(account.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(account.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(account.iban.maskIban(), style = MaterialTheme.typography.bodyMedium)
            Text(
                account.balance.tl(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* ---------------- screen ---------------- */

@Composable
fun AccountsScreen(
    onAccountClick: (String) -> Unit,
    onOpenCardDetail: () -> Unit = {}
) {
    val vm: AccountsViewModel = viewModel()
    val accounts by vm.accounts.collectAsState()
    val loading  by vm.loading.collectAsState()
    val error    by vm.error.collectAsState()
    val userId   by vm.userId.collectAsState()

    val total = accounts.sumOf { it.balance }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Hesaplar") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccountsStatusBar(
                userId = userId,
                totalBalance = total,
                loading = loading,
                error = error,
                onRefresh = vm::refresh
            )

            when {
                accounts.isEmpty() && !loading && userId == null -> {
                    Text("Hesap yok. Üstten “Bağlan (u1)”a basın, sonra Yenile’ye dokunun.")
                }
                accounts.isEmpty() && !loading -> {
                    Text("Hesap bulunamadı.")
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(accounts) { acc ->
                            AccountCard(acc, onClick = onAccountClick)
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}
