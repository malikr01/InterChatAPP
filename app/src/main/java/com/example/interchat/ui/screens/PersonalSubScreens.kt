package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Ortak iskelet: üstte geri ikonlu AppBar + içerik */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenWithBack(
    title: String,
    onBack: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

/** 1) Hesap Bakiyesi */
@Composable
fun BalanceScreen(onBack: () -> Unit) {
    val accounts = listOf(
        "Vadesiz (TRY) — 12.450,00 ₺",
        "Döviz (USD) — 1.230,50 $",
        "Vadeli (TRY) — 250.000,00 ₺"
    )
    ScreenWithBack(title = "Hesap Bakiyesi", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(accounts) { row ->
                Card(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(row, Modifier.padding(16.dp))
                }
            }
        }
    }
}

/** 2) Hesap Hareketleri & Geçmiş */
@Composable
fun TransactionsScreen(onBack: () -> Unit) {
    val tx = listOf(
        "12.08  POS - Market  -239,90 ₺",
        "12.08  EFT +1.500,00 ₺ (Ali Yılmaz)",
        "11.08  FAST -120,00 ₺"
    )
    ScreenWithBack(title = "Hesap Hareketleri", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(tx) { row ->
                ListItem(headlineContent = { Text(row) })
                Divider()
            }
        }
    }
}

/** 3) Kart Limitleri & Borç */
@Composable
fun CardInfoScreen(onBack: () -> Unit) {
    ScreenWithBack(title = "Kart Bilgileri", onBack = onBack) {
        Card {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Kart Limiti: 40.000 ₺")
                Text("Kullanılabilir Limit: 27.350 ₺")
                Text("Dönem Borcu: 12.650 ₺")
                Text("Son Ödeme Tarihi: 25.08.2025")
            }
        }
    }
}

/** 4) Son İşlemler & Durum */
@Composable
fun RecentOpsScreen(onBack: () -> Unit) {
    val ops = listOf(
        "Kredi Başvurusu — İnceleniyor",
        "HGS Otomatik Yükleme — Başarılı",
        "Elektrik Faturası — Ödendi"
    )
    ScreenWithBack(title = "Son İşlemler", onBack = onBack) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(ops) { r ->
                Card(Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(r, Modifier.padding(16.dp))
                }
            }
        }
    }
}
