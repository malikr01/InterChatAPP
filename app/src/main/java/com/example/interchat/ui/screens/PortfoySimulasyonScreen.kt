package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YalinYatirimScreen(onBack: () -> Unit) {
    var tutarText by remember { mutableStateOf("100000") }
    var yilText   by remember { mutableStateOf("5") }
    var aylikKatkiText by remember { mutableStateOf("2000") }

    var oranYillik by remember { mutableStateOf(0.0) }
    var sonDeger   by remember { mutableStateOf<Double?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Basit Yatırım") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = tutarText,
                onValueChange = { tutarText = it },
                label = { Text("Başlangıç Tutarı (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = yilText,
                onValueChange = { yilText = it },
                label = { Text("Süre (Yıl)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = aylikKatkiText,
                onValueChange = { aylikKatkiText = it },
                label = { Text("Aylık Katkı (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val baslangic = tutarText.toDoubleOrNull() ?: 0.0
                    val yil       = yilText.toIntOrNull() ?: 0
                    val katkı     = aylikKatkiText.toDoubleOrNull() ?: 0.0

                    val rateYillik = yilBazliFaizOrani(yil)     // örn. 0.12
                    val rateAylik  = (1 + rateYillik).pow(1.0/12.0) - 1.0

                    oranYillik = rateYillik
                    sonDeger = hesaplaAylikBilesik(baslangic, katkı, yil, rateAylik)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Hesapla") }

            Divider()

            sonDeger?.let { v ->
                val baslangic = tutarText.toDoubleOrNull() ?: 0.0
                val yil       = yilText.toIntOrNull() ?: 0
                val katkı     = aylikKatkiText.toDoubleOrNull() ?: 0.0
                val toplamNakitGiris = baslangic + (katkı * (yil * 12))
                val kar = v - toplamNakitGiris

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text("Yıl: $yil") })
                    AssistChip(onClick = {}, label = { Text("Yıllık Oran: ${(oranYillik*100).format2()}%") })
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text("Son Değer: ${v.formatTL()}") })
                    AssistChip(onClick = {}, label = { Text("Toplam Kâr: ${kar.formatTL()}") })
                }
            }
        }
    }
}

/** Yıla göre kademeli yıllık oran – basit örnek politika */
private fun yilBazliFaizOrani(yil: Int): Double = when {
    yil <= 0  -> 0.0
    yil <= 2  -> 0.10   // %10
    yil <= 5  -> 0.12   // %12
    yil <= 10 -> 0.15   // %15
    else      -> 0.18   // %18
}

/** Aylık bileşik: her ay önce faiz uygulanır sonra katkı eklenir */
private fun hesaplaAylikBilesik(
    baslangic: Double,
    aylikKatki: Double,
    yil: Int,
    aylikOran: Double
): Double {
    val ay = yil * 12
    var v = baslangic
    repeat(ay) {
        v *= (1 + aylikOran)
        v += aylikKatki
    }
    return v
}

private fun Double.format2(): String = String.format("%,.2f", this)
private fun Double.formatTL(): String = String.format("%,.2f TL", this)
