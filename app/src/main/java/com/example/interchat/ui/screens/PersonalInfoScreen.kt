package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PersonalInfoScreen(
    onOpenBalance: () -> Unit,
    onOpenTx: () -> Unit,
    onOpenCard: () -> Unit,
    onOpenRecent: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kişisel Bilgi Sorgulama",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = onOpenBalance, modifier = Modifier.fillMaxWidth()) {
            Text("Hesap Bakiyesi")
        }

        Button(onClick = onOpenTx, modifier = Modifier.fillMaxWidth()) {
            Text("Hesap Hareketleri & Geçmiş İşlemler")
        }

        Button(onClick = onOpenCard, modifier = Modifier.fillMaxWidth()) {
            Text("Kart Limitleri & Borç Bilgileri")
        }

        Button(onClick = onOpenRecent, modifier = Modifier.fillMaxWidth()) {
            Text("Son Yapılan İşlemler")
        }
    }
}
