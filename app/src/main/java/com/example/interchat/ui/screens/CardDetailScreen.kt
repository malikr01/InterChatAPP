package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(onBack: () -> Unit) {
    val cardMasked = "5324 •••• •••• 2741"
    val statementDebt = "12.450,00 ₺"
    val minPayment = "3.735,00 ₺"
    val availableLimit = "7.550,00 ₺"
    val totalLimit = "20.000,00 ₺"
    val dueDate = "25 Ağustos 2025"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kredi Kartı Detayı") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Geri") }
                }
            )
        }
    ) { p ->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(cardMasked, style = MaterialTheme.typography.titleMedium)
                    Text("Hesap Özeti Borcu: $statementDebt", style = MaterialTheme.typography.titleMedium)
                    Text("Asgari Ödeme: $minPayment")
                    Text("Son Ödeme Tarihi: $dueDate")
                }
            }
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Kalan Limit: $availableLimit")
                    Text("Toplam Limit: $totalLimit")
                }
            }
            Text("Son İşlemler")
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• 12.08 Market Alışverişi  -420,75 ₺")
                    Text("• 11.08 Akaryakıt          -950,00 ₺")
                    Text("• 10.08 Restoran            -315,90 ₺")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { }) { Text("Borç Öde") }
                OutlinedButton(onClick = { }) { Text("Ekstre") }
            }
        }
    }
}
