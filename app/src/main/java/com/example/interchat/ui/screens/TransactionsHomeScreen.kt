package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
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
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri"
                            )
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

/** 0) İşlemler ana menü */
@Composable
fun TransactionsHomeScreen(
    onTransfer: () -> Unit,
    onBill: () -> Unit,
    onTopUp: () -> Unit,
    onScheduled: () -> Unit,
    onHistory: () -> Unit,
    onCalculations: () -> Unit      // ✅ Hesaplamalar için eklendi
) {
    TxScaffold(title = "İşlemler") {
        Button(onClick = onTransfer,     modifier = Modifier.fillMaxWidth()) { Text("Hızlı Transfer") }
        Button(onClick = onBill,         modifier = Modifier.fillMaxWidth()) { Text("Fatura Öde") }
        Button(onClick = onTopUp,        modifier = Modifier.fillMaxWidth()) { Text("TL Yükle") }
        Button(onClick = onScheduled,    modifier = Modifier.fillMaxWidth()) { Text("Planlı Ödemeler") }
        Button(onClick = onHistory,      modifier = Modifier.fillMaxWidth()) { Text("İşlem Geçmişi") }
        Button(onClick = onCalculations, modifier = Modifier.fillMaxWidth()) { Text("Hesaplamalar") } // ✅ yeni
    }
}

/** 1) Hızlı Transfer */
@Composable
fun TransferScreen(onBack: () -> Unit) {
    var iban by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var info by remember { mutableStateOf<String?>(null) }

    TxScaffold(title = "Hızlı Transfer", onBack = onBack) {
        OutlinedTextField(iban,  { iban = it },  label = { Text("IBAN") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(name,  { name = it },  label = { Text("Alıcı Adı") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(amount,{ amount = it },label = { Text("Tutar (₺)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(note,  { note = it },  label = { Text("Açıklama (ops)") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                info = if (iban.length in 20..34 && amount.isNotBlank())
                    "Transfer talebi alındı." else "Lütfen IBAN ve tutarı kontrol edin."
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Gönder") }
        info?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

/** 2) Fatura Ödeme */
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

/** 3) TL Yükleme */
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

/** 4) Planlı Ödemeler (liste/placeholder) */
@Composable
fun ScheduledPaymentsScreen(onBack: () -> Unit) {
    val items = remember {
        mutableStateListOf(
            "Her ayın 10'u – Elektrik 400 ₺",
            "Her ayın 15'i – Su 180 ₺"
        )
    }
    TxScaffold(title = "Planlı Ödemeler", onBack = onBack) {
        LazyColumn {
            items(items) { it ->
                ListItem(headlineContent = { Text(it) })
                Divider()
            }
        }
        Button(onClick = { items += "Her ayın 20'si – İnternet 250 ₺" }, modifier = Modifier.fillMaxWidth()) {
            Text("Yeni Plan Ekle (mock)")
        }
    }
}

/** 5) İşlem Geçmişi (filtre/placeholder) */
@Composable
fun TransactionHistoryScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val history = listOf(
        "12.08  Transfer  -250,00 ₺",
        "11.08  Fatura    -180,00 ₺",
        "10.08  TL Yükleme -50,00 ₺"
    )
    val filtered = history.filter { it.contains(query, ignoreCase = true) }

    TxScaffold(title = "İşlem Geçmişi", onBack = onBack) {
        OutlinedTextField(query, { query = it }, label = { Text("Ara (tür/tutar/tarih)") }, modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(filtered) { row ->
                ListItem(headlineContent = { Text(row) })
                Divider()
            }
        }
    }
}
