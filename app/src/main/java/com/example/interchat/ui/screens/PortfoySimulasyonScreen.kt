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
import kotlin.math.*
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfoySimulasyonScreen(onBack: () -> Unit) {
    // Varsayılanlar
    var baslangicTutarText by remember { mutableStateOf("100000") } // TL
    var aylikKatkiText     by remember { mutableStateOf("2000") }   // TL
    var yilText            by remember { mutableStateOf("10") }     // yıl
    var beklenenGetiriText by remember { mutableStateOf("12") }     // yıllık %
    var volatiliteText     by remember { mutableStateOf("18") }     // yıllık %
    var simSayisiText      by remember { mutableStateOf("1000") }   // monte carlo

    // Sonuçlar
    var ozet by remember { mutableStateOf<SimOzet?>(null) }
    var ornekYol by remember { mutableStateOf<List<Double>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Portföy Simülasyonu") },
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
            // Girdiler
            OutlinedTextField(
                value = baslangicTutarText,
                onValueChange = { baslangicTutarText = it },
                label = { Text("Başlangıç Tutarı (TL)") },
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
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = yilText,
                    onValueChange = { yilText = it },
                    label = { Text("Süre (Yıl)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = simSayisiText,
                    onValueChange = { simSayisiText = it },
                    label = { Text("Simülasyon Sayısı") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = beklenenGetiriText,
                    onValueChange = { beklenenGetiriText = it },
                    label = { Text("Yıllık Beklenen Getiri (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = volatiliteText,
                    onValueChange = { volatiliteText = it },
                    label = { Text("Yıllık Volatilite (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    val baslangic = baslangicTutarText.toDoubleOrNull() ?: 0.0
                    val aylikKatki = aylikKatkiText.toDoubleOrNull() ?: 0.0
                    val yil = yilText.toIntOrNull() ?: 0
                    val muYillik = (beklenenGetiriText.toDoubleOrNull() ?: 0.0) / 100.0
                    val sigmaYillik = (volatiliteText.toDoubleOrNull() ?: 0.0) / 100.0
                    val nSim = simSayisiText.toIntOrNull() ?: 0

                    val sonuc = monteCarloPortfoySim(
                        years = yil,
                        annualMu = muYillik,
                        annualSigma = sigmaYillik,
                        initial = baslangic,
                        monthlyContribution = aylikKatki,
                        simulations = nSim,
                        seed = 42
                    )
                    ozet = sonuc.ozet
                    ornekYol = sonuc.samplePath
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simüle Et") }

            // Sonuç Özeti
            ozet?.let { s ->
                val toplamKatki = (aylikKatkiText.toDoubleOrNull() ?: 0.0) * (12 * (yilText.toIntOrNull() ?: 0))
                val toplamNakitGiris = (baslangicTutarText.toDoubleOrNull() ?: 0.0) + toplamKatki

                Spacer(Modifier.height(8.dp))
                FlowRowMainAxisSpaced {
                    AssistChip(onClick = {}, label = { Text("Medyan Son Değer: ${s.median.format2()} TL") })
                    AssistChip(onClick = {}, label = { Text("P5: ${s.p5.format2()} TL") })
                    AssistChip(onClick = {}, label = { Text("P95: ${s.p95.format2()} TL") })
                    AssistChip(onClick = {}, label = { Text("En Kötü: ${s.min.format2()} TL") })
                    AssistChip(onClick = {}, label = { Text("En İyi: ${s.max.format2()} TL") })
                }

                Spacer(Modifier.height(8.dp))
                FlowRowMainAxisSpaced {
                    AssistChip(onClick = {}, label = { Text("Toplam Nakit Giriş: ${toplamNakitGiris.format2()} TL") })
                    AssistChip(onClick = {}, label = { Text("Medyan Kâr: ${(s.median - toplamNakitGiris).format2()} TL") })
                    AssistChip(onClick = {}, label = { Text("Beklenen (ortalama): ${s.mean.format2()} TL") })
                }

                Divider(Modifier.padding(vertical = 8.dp))

                Text("Örnek Yol (aylık değerler):", style = MaterialTheme.typography.titleMedium)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f, fill = false)) {
                    items(ornekYol.withIndex().toList()) { (i, v) ->
                        Text("${i + 1}. Ay: ${v.format2()} TL")
                    }
                }
            }
        }
    }
}

/* ---------- Hesaplama Katmanı ---------- */

private data class SimSonuc(
    val finals: DoubleArray,
    val samplePath: List<Double>,
    val ozet: SimOzet
)

private data class SimOzet(
    val mean: Double,
    val median: Double,
    val p5: Double,
    val p95: Double,
    val min: Double,
    val max: Double
)

/**
 * Geometric Brownian Motion (aylık adımlarla) + aylık katkı ekleyerek portföy simülasyonu.
 * Not: Aylık katkı, her ayın sonunda portföy değerine eklenir (yaklaşık).
 */
private fun monteCarloPortfoySim(
    years: Int,
    annualMu: Double,
    annualSigma: Double,
    initial: Double,
    monthlyContribution: Double,
    simulations: Int,
    seed: Int
): SimSonuc {
    val months = years * 12
    if (years <= 0 || simulations <= 0 || initial < 0 || monthlyContribution < 0) {
        return SimSonuc(DoubleArray(0), emptyList(), SimOzet(0.0,0.0,0.0,0.0,0.0,0.0))
    }

    val dt = 1.0 / 12.0
    val mu = annualMu
    val sigma = annualSigma
    val drift = (mu - 0.5 * sigma * sigma) * dt
    val vol   = sigma * sqrt(dt)

    val rng = Random(seed)
    val finals = DoubleArray(simulations)
    var samplePath: List<Double> = emptyList()

    repeat(simulations) { sim ->
        var v = initial
        val path = if (sim == 0) ArrayList<Double>(months) else null

        for (m in 0 until months) {
            val z = stdNormal(rng)
            val growth = exp(drift + vol * z)
            v *= growth
            v += monthlyContribution
            if (path != null) path.add(v)
        }

        finals[sim] = v
        if (sim == 0) samplePath = path!!.toList()
    }

    finals.sort()
    val mean = finals.average()
    val median = percentile(finals, 50.0)
    val p5  = percentile(finals, 5.0)
    val p95 = percentile(finals, 95.0)
    val min = finals.firstOrNull() ?: 0.0
    val max = finals.lastOrNull() ?: 0.0

    return SimSonuc(
        finals = finals,
        samplePath = samplePath,
        ozet = SimOzet(mean, median, p5, p95, min, max)
    )
}

private fun percentile(sorted: DoubleArray, p: Double): Double {
    if (sorted.isEmpty()) return 0.0
    val r = (p / 100.0) * (sorted.size - 1)
    val i = floor(r).toInt()
    val frac = r - i
    return if (i + 1 < sorted.size) sorted[i] * (1 - frac) + sorted[i + 1] * frac else sorted[i].toDouble()
}

private fun stdNormal(rng: Random): Double {
    // Box–Muller
    val u1 = rng.nextDouble().coerceIn(1e-12, 1.0) // 0 olmasın
    val u2 = rng.nextDouble()
    return sqrt(-2.0 * ln(u1)) * cos(2.0 * Math.PI * u2)
}

private fun Double.format2(): String = String.format("%,.2f", this)

/* Basit flow row - uzun chipleri sarar */
@Composable
private fun FlowRowMainAxisSpaced(content: @Composable RowScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}
