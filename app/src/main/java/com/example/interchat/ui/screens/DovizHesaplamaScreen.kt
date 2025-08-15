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
import kotlin.math.round

private const val USD_RATE = 40.89   // 1 USD = 40.89 TL
private const val EUR_RATE = 47.76   // 1 EUR = 47.76 TL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DovizHesaplamaScreen(onBack: () -> Unit) {
    var yon by remember { mutableStateOf(0) } // 0: TL -> FX, 1: FX -> TL
    var paraBirimi by remember { mutableStateOf(0) } // 0: USD, 1: EUR
    var tutarText by remember { mutableStateOf("1000") }
    var sonuc by remember { mutableStateOf<Double?>(null) }

    val rate = if (paraBirimi == 0) USD_RATE else EUR_RATE
    val birimStr = if (paraBirimi == 0) "USD" else "EUR"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Döviz & Kur Hesaplama") },
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
            // Yön seçimi
            SegmentedButtons(
                options = listOf("TL → $birimStr", "$birimStr → TL"),
                selected = yon,
                onSelected = { yon = it; sonuc = null }
            )

            // Para birimi seçimi
            SegmentedButtons(
                options = listOf("USD", "EUR"),
                selected = paraBirimi,
                onSelected = { i -> paraBirimi = i; sonuc = null }
            )

            // Tutar
            OutlinedTextField(
                value = tutarText,
                onValueChange = { tutarText = it },
                label = {
                    Text(
                        if (yon == 0) "Tutar (TL)"
                        else "Tutar ($birimStr)"
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Hesapla
            Button(
                onClick = {
                    val x = tutarText.replace(",", ".").toDoubleOrNull() ?: 0.0
                    sonuc = if (yon == 0) x / rate else x * rate
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Hesapla") }

            // Sonuç
            sonuc?.let { v ->
                ElevatedCard {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Kur bilgisi: 1 $birimStr = ${rate.format2()} TL")
                        if (yon == 0) {
                            Text("Sonuç: ${(v).format2()} $birimStr")
                        } else {
                            Text("Sonuç: ${(v).format2()} TL")
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            AssistChip(onClick = {}, label = { Text("Güncel kur (sabit): USD ${USD_RATE.format2()} • EUR ${EUR_RATE.format2()}") })
        }
    }
}

/* ---- Küçük yardımcılar ---- */

@Composable
private fun SegmentedButtons(
    options: List<String>,
    selected: Int,
    onSelected: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = index == selected,
                onClick = { onSelected(index) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size)
            ) {
                Text(label)
            }
        }
    }
}

private fun Double.format2(): String = String.format("%,.2f", this)
