package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import kotlin.math.pow

data class OdemePlaniItem(
    val ay: Int,
    val taksit: Double,
    val anapara: Double,
    val faiz: Double,
    val kalanAnapara: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdemePlanlariScreen(onBack: () -> Unit) {
    var krediTutariText by remember { mutableStateOf("100000") }
    var faizOraniYillikText by remember { mutableStateOf("24") } // %24
    var vadeAyText by remember { mutableStateOf("12") }

    var plan by remember { mutableStateOf(emptyList<OdemePlaniItem>()) }
    var aylikTaksit by remember { mutableStateOf(0.0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ödeme Planları") },
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
            // Girdi alanları
            OutlinedTextField(
                value = krediTutariText,
                onValueChange = { krediTutariText = it },
                label = { Text("Kredi Tutarı (TL)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = faizOraniYillikText,
                onValueChange = { faizOraniYillikText = it },
                label = { Text("Yıllık Faiz Oranı (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = vadeAyText,
                onValueChange = { vadeAyText = it },
                label = { Text("Vade (Ay)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val tutar = krediTutariText.toDoubleOrNull() ?: 0.0
                    val faizYuzde = (faizOraniYillikText.toDoubleOrNull() ?: 0.0)
                    val vade = vadeAyText.toIntOrNull() ?: 0
                    val (list, taksit) = krediOdemePlani(tutar, faizYuzde / 100.0, vade)
                    plan = list
                    aylikTaksit = taksit
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hesapla")
            }

            if (plan.isNotEmpty()) {
                // Özet
                val toplamOdeme = plan.sumOf { it.taksit }
                val toplamFaiz = toplamOdeme - (krediTutariText.toDoubleOrNull() ?: 0.0)
                AssistChip(
                    onClick = {},
                    label = { Text("Aylık Taksit: ${aylikTaksit.format2()} TL") }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("Toplam Ödeme: ${toplamOdeme.format2()} TL") }
                )
                AssistChip(
                    onClick = {},
                    label = { Text("Toplam Faiz: ${toplamFaiz.format2()} TL") }
                )

                Divider()

                // Tablo
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            "Amortisman Tablosu",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(plan) { item ->
                        ElevatedCard {
                            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Ay ${item.ay}")
                                Text("Taksit: ${item.taksit.format2()} TL")
                                Text("Anapara: ${item.anapara.format2()} TL")
                                Text("Faiz: ${item.faiz.format2()} TL")
                                Text("Kalan: ${item.kalanAnapara.coerceAtLeast(0.0).format2()} TL")
                            }
                        }
                    }
                    item { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }
}

/**
 * Anüite (eşit taksit) yöntemiyle ödeme planı üretir.
 * @param krediTutari Ödenen kredi anaparası
 * @param faizOraniYillik Yıllık nominal faiz (ör. %24 → 0.24)
 * @param vadeAy Vade ay sayısı
 * @return Pair<List<OdemePlaniItem>, AylıkTaksit>
 */
private fun krediOdemePlani(
    krediTutari: Double,
    faizOraniYillik: Double,
    vadeAy: Int
): Pair<List<OdemePlaniItem>, Double> {
    if (krediTutari <= 0 || faizOraniYillik < 0 || vadeAy <= 0) return Pair(emptyList(), 0.0)

    val aylikFaiz = faizOraniYillik / 12.0
    val taksit = if (aylikFaiz == 0.0) {
        krediTutari / vadeAy
    } else {
        (krediTutari * aylikFaiz) / (1 - (1 + aylikFaiz).pow(-vadeAy))
    }

    val plan = mutableListOf<OdemePlaniItem>()
    var kalan = krediTutari

    for (ay in 1..vadeAy) {
        val faiz = kalan * aylikFaiz
        val anapara = taksit - faiz
        kalan -= anapara
        plan += OdemePlaniItem(
            ay = ay,
            taksit = taksit,
            anapara = anapara,
            faiz = faiz,
            kalanAnapara = kalan.coerceAtLeast(0.0)
        )
    }
    return Pair(plan, taksit)
}

private fun Double.format2(): String = String.format("%,.2f", this)
