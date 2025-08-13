package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FaqScreen() {
    Scaffold { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Sıkça Sorulan Sorular", style = MaterialTheme.typography.headlineSmall)
            ListItem(headlineContent = { Text("Kart borcumu nereden öderim?") },
                supportingContent = { Text("İşlemler > Fatura Öde menüsünden.") })
            Divider()
            ListItem(headlineContent = { Text("Hesap hareketleri nerede?") },
                supportingContent = { Text("Kişisel Bilgi > Hesap Hareketleri ekranında.") })
            Divider()
        }
    }
}
