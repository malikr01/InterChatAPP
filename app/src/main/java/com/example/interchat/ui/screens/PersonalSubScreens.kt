package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
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

/** Kişisel Bilgi > Bakiye detayı (placeholder) */
@Composable
fun BalanceScreen(onBack: () -> Unit) = ScreenWithBack("Bakiye", onBack) {
    Text("Mevcut bakiye: 12.345,67 ₺", style = MaterialTheme.typography.titleMedium)
    Text("Bu ekranı kendi verinle doldurabilirsin.")
}

/** Kişisel Bilgi > Hesap hareketleri (placeholder) */
@Composable
fun TransactionsScreen(onBack: () -> Unit) = ScreenWithBack("Hesap Hareketleri", onBack) {
    val rows = remember {
        listOf(
            "12.08  Market           -350,00 ₺",
            "11.08  Maaş            +45.000,00 ₺",
            "10.08  Fatura (Su)     -180,00 ₺"
        )
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rows) { r -> ListItem(headlineContent = { Text(r) }); Divider() }
    }
}

/** Kişisel Bilgi > Kart bilgileri (placeholder) */
@Composable
fun CardInfoScreen(onBack: () -> Unit) = ScreenWithBack("Kart Bilgileri", onBack) {
    Text("Kart: 1234 •••• •••• 5678")
    Text("Son kullanma: 12/27")
    Text("Limit: 30.000 ₺")
}

/** Kişisel Bilgi > Son işlemler (placeholder) */
@Composable
fun RecentOpsScreen(onBack: () -> Unit) = ScreenWithBack("Son İşlemler", onBack) {
    val rows = remember { (1..10).map { "İşlem #$it - örnek" } }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rows) { r -> ListItem(headlineContent = { Text(r) }); Divider() }
    }
}
