// app/src/main/java/com/example/interchat/ui/screens/HomeScreen.kt
package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ana Sayfa") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Çıkış"
                        )
                    }
                }
            )
        }
    ) { pad ->
        // ⬇️ Buradaki gövdeyi kendi mevcut içeriğinle aynı bırak.
        // Eğer önceden farklı UI vardıysa sadece şu Box bloğunu
        // kendi içeriklerinle değiştir (üst bar ve logout kalır).
        Box(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Hoş geldin! (Home)")
        }
    }
}
