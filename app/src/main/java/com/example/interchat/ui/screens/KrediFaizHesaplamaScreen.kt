@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.interchat.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.pow

@Composable
fun KrediFaizHesaplamaScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kredi Faiz Hesaplama") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            var tutar by remember { mutableStateOf("50000") }
            var vadeAy by remember { mutableStateOf("12") }
            var aylikOran by remember { mutableStateOf("3.69") } // Aylık % oran

            OutlinedTextField(
                value = tutar,
                onValueChange = { tutar = it },
                label = { Text("Kredi Tutarı (TL)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vadeAy,
                onValueChange = { vadeAy = it },
                label = { Text("Vade (Ay)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            OutlinedTextField(
                value = aylikOran,
                onValueChange = { aylikOran = it },
                label = { Text("Aylık Faiz (%)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            val p = tutar.toDoubleOrNull()
            val n = vadeAy.toIntOrNull()
            val rPct = aylikOran.toDoubleOrNull()

            if (p != null && n != null && rPct != null && p > 0 && n > 0 && rPct > 0) {
                val r = rPct / 100.0
                val f = (1 + r).pow(n)
                val taksit = p * r * f / (f - 1)
                val toplam = taksit * n
                val toplamFaiz = toplam - p

                Spacer(Modifier.height(12.dp))
                Text("Aylık Taksit: ₺%,.2f".format(taksit))
                Text("Toplam Geri Ödeme: ₺%,.2f".format(toplam))
                Text("Toplam Faiz: ₺%,.2f".format(toplamFaiz))
            }
        }
    }
}
