@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.Account
import java.text.NumberFormat
import java.util.Locale

/* ---- Para & IBAN yardımcıları ---- */
private val moneyTr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("tr","TR"))
private fun Double.tl(): String = moneyTr.format(this)
private fun String.maskIban(): String {
    val clean = replace(" ", "")
    if (clean.length < 12) return this
    val head = clean.take(6)
    val tail = clean.takeLast(4)
    return (head + "**************" + tail).chunked(4).joinToString(" ")
}

/* ---- Bağlantı durumu barı ---- */
@Composable
private fun AccountsStatusBar(vm: AccountsViewModel, totalBalance: Double) {
    val loading = vm.loading.collectAsState().value
    val error   = vm.error.collectAsState().value
    val uid     = vm.userId.collectAsState().value
    val count   = vm.accounts.collectAsState().value.size

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(if (uid != null) "Bağlı: $uid (mock)" else "Bağlı değil") })
            AssistChip(onClick = {}, label = { Text("Hesap sayısı: $count") })
            AssistChip(onClick = {}, label = { Text("Toplam: ${totalBalance.tl()}") })
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = { vm.refresh() }, enabled = !loading) { Text("Yenile") }
        }
        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (error != null) Text("Hata: $error", color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { UserSession.setUserId("u1") }) { Text("Bağlan (u1)") }
            TextButton(onClick = { UserSession.setUserId("u2") }) { Text("Bağlan (u2)") }
            TextButton(onClick = { UserSession.setUserId(null) }) { Text("Bağlantıyı Kes") }
        }
        Divider()
    }
}

/* ---- Tek hesap kartı ---- */
@Composable
private fun AccountCard(account: Account, onClick: (String) -> Unit) {
    Card(
        onClick = { onClick(account.id) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        ListItem(
            headlineContent   = { Text(account.name, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(account.iban.maskIban()) },
            trailingContent   = { Text(account.balance.tl(), fontWeight = FontWeight.SemiBold) }
        )
    }
}

/* ---- Ekran ---- */
@Composable
fun AccountsScreen(
    onAccountClick: (String) -> Unit,
    onOpenCardDetail: () -> Unit = {}
) {
    val vm: AccountsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val accounts = vm.accounts.collectAsState().value
    val total = accounts.sumOf { it.balance }

    Scaffold(topBar = { TopAppBar(title = { Text("Hesap Özeti") }) }) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AccountsStatusBar(vm, total)

            OutlinedButton(onClick = onOpenCardDetail, modifier = Modifier.fillMaxWidth()) {
                Text("Kart Detayına Git")
            }

            if (accounts.isEmpty()) {
                Text("Hesap bulunamadı veya bağlı değilsiniz.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(accounts, key = { it.id }) { acc ->
                        AccountCard(account = acc, onClick = onAccountClick)
                    }
                }
            }
        }
    }
}
