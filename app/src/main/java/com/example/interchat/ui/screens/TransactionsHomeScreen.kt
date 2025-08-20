@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.interchat.ui.screens

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
import com.example.interchat.data.session.ScheduledPaymentsStore
import com.example.interchat.data.session.UserSession
import com.example.interchat.domain.finance.TxType
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
            AssistChip(onClick = {}, label = { Text(if (uid != null) "Bağlı: $uid (mock)" else "Bağlı değil") })
            AssistChip(onClick = {}, label = { Text("İşlem sayısı: $txCount") })
            AssistChip(onClick = {}, label = { Text("Planlı: $scheduledCount") }) // ⬅ planlı sayaç
            Spacer(modifier = Modifier.weight(1f))
            OutlinedButton(onClick = { vm.refresh() }, enabled = !loading) {
                Text("Yenile") // ⬅ geri geldi
            }
        }
        if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (error != null) Text("Hata: $error", color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { UserSession.setUserId("u1") }) { Text("Bağlan (Mock u1)") }
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
    val vm: TransactionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    TxScaffold(title = "İşlemler") {
        // Bağlı kullanıcı + sayaç + yenile
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
    val tVm: TransferViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val aVm: AccountsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // hesaplar (AccountsStore üzerinden)
    val accounts = aVm.accounts.collectAsState().value
    val loading  = tVm.loading.collectAsState().value
    val result   = tVm.result.collectAsState().value
    val error    = tVm.error.collectAsState().value

    var selectedId by remember { mutableStateOf(accounts.firstOrNull()?.id.orEmpty()) }
    var toIban    by remember { mutableStateOf("") }
    var title     by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }

    TxScaffold(title = "Hızlı Transfer", onBack = onBack) {
        // Hesap seçimi
        if (accounts.isEmpty()) {
            Text("Hesabınız bulunamadı. Bağlanın veya Hesaplar sekmesinden seed olun.", color = MaterialTheme.colorScheme.error)
        } else {
            val sel = accounts.firstOrNull { it.id == selectedId } ?: accounts.first()
            selectedId = sel.id
            Text("Kaynak Hesap: ${sel.name} • Bakiye: %,.2f ₺".format(sel.balance), fontWeight = FontWeight.SemiBold)
        }

        OutlinedTextField(value = toIban, onValueChange = { toIban = it },
            label = { Text("Alıcı IBAN") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = title, onValueChange = { title = it },
            label = { Text("Alıcı Adı / Açıklama") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amountStr, onValueChange = { amountStr = it },
            label = { Text("Tutar (₺)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        // basit seçim için butonlar
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


/* ------------------- 2) Fatura Ödeme ------------------- */
@Composable
fun BillPaymentScreen(onBack: () -> Unit) {
    var category by remember { mutableStateOf("Elektrik") }
    var subscriber by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    TxScaffold(title = "Fatura Ödeme", onBack = onBack) {
        OutlinedTextField(category, { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(subscriber,{ subscriber = it }, label = { Text("Abone No") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount,   { amount = it },    label = { Text("Tutar (₺)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(onClick = { result = "Ödeme talebi alındı." }, modifier = Modifier.fillMaxWidth()) { Text("Öde") }
        result?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

/* ------------------- 3) TL Yükleme ------------------- */
@Composable
fun TopUpScreen(onBack: () -> Unit) {
    var phone by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<String?>(null) }

    TxScaffold(title = "TL Yükleme", onBack = onBack) {
        OutlinedTextField(phone,  { phone = it },  label = { Text("Telefon No") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount, { amount = it }, label = { Text("Tutar (₺)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(onClick = { result = "Yükleme talebi alındı." }, modifier = Modifier.fillMaxWidth()) { Text("Yükle") }
        result?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

/* ------------------- 4) Planlı Ödemeler (store bağlı) ------------------- */
@Composable
fun ScheduledPaymentsScreen(onBack: () -> Unit) {
    val uid = UserSession.userId.collectAsState().value
    LaunchedEffect(uid) { ScheduledPaymentsStore.onUserChanged(uid) }
    val items = ScheduledPaymentsStore.items.collectAsState().value

    TxScaffold(title = "Planlı Ödemeler", onBack = onBack) {
        if (items.isEmpty()) {
            Text("Planlı ödeme bulunamadı.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(items) { it ->
                    ListItem(
                        headlineContent = { Text(it.title) },
                        supportingContent = { Text("Her ayın ${it.dayOfMonth}'u") },
                        trailingContent   = { Text("%,.2f ₺".format(it.amount)) }
                    )
                    Divider()
                }
            }
        }
        Button(
            onClick = { ScheduledPaymentsStore.addMock() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Yeni Plan Ekle (mock)") }
    }
}

/* ------------------- Para format helper ------------------- */
private val moneyTr: NumberFormat = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
private fun Double.formatTL(): String = moneyTr.format(this)

/* ------------------- 5) İşlem Geçmişi ------------------- */
@Composable
fun TransactionHistoryScreen(onBack: () -> Unit) {
    val vm: TransactionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val all = vm.items.collectAsState().value

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf<TxType?>(null) }

    val filtered = all.filter { row ->
        val q = query.trim()
        val byText = if (q.isBlank()) true else
            row.title.contains(q, ignoreCase = true) || row.date.toString().contains(q)
        val byType = active?.let { it == row.type } ?: true
        byText && byType
    }

    TxScaffold(title = "İşlem Geçmişi", onBack = onBack) {
        ConnectionStatusBar(vm) // üstte de göstermek istersen

        OutlinedTextField(
            value = query, onValueChange = { query = it },
            label = { Text("Ara (ad/tarih/tip)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = active == null, onClick = { active = null }, label = { Text("Tümü") })
            FilterChip(selected = active == TxType.Transfer, onClick = { active = TxType.Transfer }, label = { Text("Transfer") })
            FilterChip(selected = active == TxType.Bill,     onClick = { active = TxType.Bill },     label = { Text("Fatura") })
            FilterChip(selected = active == TxType.TopUp,    onClick = { active = TxType.TopUp },    label = { Text("TL Yükleme") })
        }

        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(filtered, key = { it.id }) { tx ->
                ListItem(
                    headlineContent   = { Text(tx.title, fontWeight = FontWeight.SemiBold) },
                    supportingContent = { Text(tx.date.toString()) },
                    trailingContent   = {
                        val color = if (tx.amount < 0) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                        Text("%,.2f ₺".format(tx.amount), color = color, fontWeight = FontWeight.SemiBold)
                    }
                )
                Divider()
            }
        }
    }
}
