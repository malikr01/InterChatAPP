@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.interchat.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interchat.data.session.ScheduledPaymentsStore
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.Account
import com.example.interchat.domain.finance.ScheduledPayment
import com.example.interchat.domain.finance.Transaction
import java.text.NumberFormat
import java.util.Locale

/* ------------------- Ortak Scaffold ------------------- */
@Composable
private fun TxScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                        }
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

/* ------------------- Bağlantı Durumu Barı ------------------- */
@Composable
private fun ConnectionStatusBar(vm: TransactionsViewModel) {
    val loading = vm.loading.collectAsState().value
    val error   = vm.error.collectAsState().value
    val uid     = vm.userId.collectAsState().value
    val txCount = vm.items.collectAsState().value.size

    // Planlı ödemeler store'u (kalıcı, per-user)
    LaunchedEffect(uid) { ScheduledPaymentsStore.onUserChanged(uid) }
    val scheduledCount = ScheduledPaymentsStore.items.collectAsState().value.size

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text(if (uid != null) "Bağlı: $uid" else "Bağlı değil") })
            AssistChip(onClick = {}, label = { Text("İşlem sayısı: $txCount") })
            AssistChip(onClick = {}, label = { Text("Planlı: $scheduledCount") })
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { vm.refresh() }, enabled = !loading) {
                Text("Yenile")
            }
        }
        if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (error != null) Text("Hata: $error", color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { UserSession.setUserId("u1") }) { Text("Bağlan (u1)") }
            TextButton(onClick = { UserSession.setUserId(null) }) { Text("Bağlantıyı Kes") }
        }
        Divider()
    }
}

/* ------------------- 0) İşlemler ana menü ------------------- */
@Composable
fun TransactionsHomeScreen(
    onTransfer: () -> Unit,
    onBill: () -> Unit,
    onTopUp: () -> Unit,
    onScheduled: () -> Unit,
    onHistory: () -> Unit,
    onCalculations: () -> Unit
) {
    val vm: TransactionsViewModel = viewModel()

    TxScaffold(title = "İşlemler") {
        ConnectionStatusBar(vm)

        Button(onClick = onTransfer,     modifier = Modifier.fillMaxWidth()) { Text("Hızlı Transfer") }
        Button(onClick = onBill,         modifier = Modifier.fillMaxWidth()) { Text("Fatura Öde") }
        Button(onClick = onTopUp,        modifier = Modifier.fillMaxWidth()) { Text("TL Yükle") }
        Button(onClick = onScheduled,    modifier = Modifier.fillMaxWidth()) { Text("Planlı Ödemeler") }
        Button(onClick = onHistory,      modifier = Modifier.fillMaxWidth()) { Text("İşlem Geçmişi") }
        Button(onClick = onCalculations, modifier = Modifier.fillMaxWidth()) { Text("Hesaplamalar") }
    }
}

/* ------------------- 1) Hızlı Transfer ------------------- */
@Composable
fun TransferScreen(onBack: () -> Unit) {
    val tVm: TransferViewModel = viewModel()
    val aVm: AccountsViewModel = viewModel()

    val accounts = aVm.accounts.collectAsState().value
    val loading  = tVm.loading.collectAsState().value
    val result   = tVm.result.collectAsState().value
    val error    = tVm.error.collectAsState().value

    var selectedId by remember { mutableStateOf(accounts.firstOrNull()?.id.orEmpty()) }
    var toIban    by remember { mutableStateOf("") }
    var title     by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }

    TxScaffold(title = "Hızlı Transfer", onBack = onBack) {
        if (accounts.isEmpty()) {
            Text("Hesabınız bulunamadı. 'Bağlan (u1)' yapın veya Hesaplar sekmesinden seed olun.", color = MaterialTheme.colorScheme.error)
        } else {
            val sel = accounts.firstOrNull { it.id == selectedId } ?: accounts.first()
            selectedId = sel.id
            Text("Kaynak Hesap: ${sel.name} • Bakiye: ${sel.balance.tl()}", fontWeight = FontWeight.SemiBold)
        }

        OutlinedTextField(value = toIban, onValueChange = { toIban = it },
            label = { Text("Alıcı IBAN") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = title, onValueChange = { title = it },
            label = { Text("Alıcı Adı / Açıklama") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amountStr, onValueChange = { amountStr = it },
            label = { Text("Tutar (₺)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            accounts.take(3).forEach { acc ->
                OutlinedButton(onClick = { selectedId = acc.id }) { Text(acc.name) }
            }
        }

        Button(
            onClick = {
                val amt = amountStr.replace(',', '.').toDoubleOrNull()
                if (selectedId.isNotBlank() && !toIban.isBlank() && (amt != null && amt > 0)) {
                    tVm.doTransfer(selectedId, toIban.trim(), title.trim(), amt)
                }
            },
            enabled = !loading && selectedId.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (loading) "Gönderiliyor..." else "Gönder") }

        when {
            result != null -> Text(result, color = MaterialTheme.colorScheme.primary)
            error  != null -> Text("Hata: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}

/* ------------------- 2) Fatura Ödeme (basit mock UI) ------------------- */
@Composable
fun BillPaymentScreen(onBack: () -> Unit) {
    TxScaffold(title = "Fatura Öde", onBack = onBack) {
        var kurum by remember { mutableStateOf("") }
        var abone by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }

        OutlinedTextField(kurum, { kurum = it }, label = { Text("Kurum (örn. Elektrik)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(abone, { abone = it }, label = { Text("Abone No") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount, { amount = it }, label = { Text("Tutar (₺)") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = { /* TODO: dilersek TransactionExtrasStore'a 'bill' ekleyebiliriz */ },
            modifier = Modifier.fillMaxWidth()) {
            Text("Öde")
        }
        Text("Not: Bu ekran demo. Transfer ekranındaki akış tam entegre.", style = MaterialTheme.typography.bodySmall)
    }
}

/* ------------------- 3) TL Yükleme (basit mock UI) ------------------- */
@Composable
fun TopUpScreen(onBack: () -> Unit) {
    TxScaffold(title = "TL Yükle", onBack = onBack) {
        var phone by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }

        OutlinedTextField(phone, { phone = it }, label = { Text("Telefon") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount, { amount = it }, label = { Text("Tutar (₺)") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { /* TODO: extras'a topup eklenebilir */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Yükle")
        }
    }
}

/* ------------------- 4) Planlı Ödemeler ------------------- */
@Composable
fun ScheduledPaymentsScreen(onBack: () -> Unit) {
    TxScaffold(title = "Planlı Ödemeler", onBack = onBack) {
        val uid by remember { derivedStateOf { UserSession.userId.value } }
        LaunchedEffect(uid) { ScheduledPaymentsStore.onUserChanged(uid) }

        val items = ScheduledPaymentsStore.items.collectAsState().value

        if (items.isEmpty()) {
            Text("Planlı ödeme bulunamadı.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { sp ->
                    ScheduledItemCard(sp)
                }
            }
        }
    }
}

@Composable
private fun ScheduledItemCard(sp: ScheduledPayment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(sp.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Ödeme günü: ${sp.dayOfMonth}", style = MaterialTheme.typography.bodyMedium)
            Text(sp.amount.tl(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

/* ------------------- 5) İşlem Geçmişi ------------------- */
@Composable
fun TransactionHistoryScreen(onBack: () -> Unit) {
    val vm: TransactionsViewModel = viewModel()
    val items = vm.items.collectAsState().value

    TxScaffold(title = "İşlem Geçmişi", onBack = onBack) {
        ConnectionStatusBar(vm)

        if (items.isEmpty()) {
            Text("İşlem yok. 'Bağlan (u1)' yapıp 'Yenile'ye basın, ardından bir transfer deneyin.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { tx -> TransactionRow(tx) }
                item { Spacer(Modifier.height(4.dp)) }
            }
        }
    }
}

@Composable
private fun TransactionRow(tx: Transaction) {
    Row(
        Modifier.fillMaxWidth().clickable { /* detay sayfası açılabilir */ }.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(tx.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(tx.date.toString(), style = MaterialTheme.typography.bodySmall)
        }
        Text(
            tx.amount.tl(),
            style = MaterialTheme.typography.titleMedium,
            color = if (tx.amount < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
    Divider()
}

/* ------------------- Para format helper ------------------- */
private val moneyTr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
private fun Double.tl(): String = moneyTr.format(this)
