package com.example.interchat.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FinancialCalculationsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Finansal Hesaplamalar", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* Faiz hesaplama */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Faiz Hesaplamaları (Mevduat & Kredi)")
        }
        Button(onClick = { /* Kredi ödeme */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Kredi Ödeme Planı ve Maliyet Hesapları")
        }
        Button(onClick = { /* Yatırım getirisi */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Yatırım Getirisi ve Portföy Simülasyonları")
        }
        Button(onClick = { /* Döviz hesaplama */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Döviz ve Kur Hesaplamaları")
        }
    }
}
