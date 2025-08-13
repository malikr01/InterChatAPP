package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Scaffold { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ana Sayfa", style = MaterialTheme.typography.headlineSmall)
            Text("Hızlı erişim")
            Button(onClick = { /* örnek: nav to Accounts */ }, modifier = Modifier.fillMaxWidth()) {
                Text("Hesaplar")
            }
            Button(onClick = { /* örnek: nav to Transactions */ }, modifier = Modifier.fillMaxWidth()) {
                Text("İşlemler")
            }
        }
    }
}
