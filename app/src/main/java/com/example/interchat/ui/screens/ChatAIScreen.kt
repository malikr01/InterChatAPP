// app/src/main/java/com/example/interchat/ui/screens/ChatAIScreen.kt
package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.HeadsetMic
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class ChatMsg(val fromBot: Boolean, val text: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAIScreen(
    onConnectLiveSupport: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var messages by remember {
        mutableStateOf(
            listOf(ChatMsg(true, "Merhaba! Ben FinansAI, kişisel finans asistanınız. Size nasıl yardımcı olabilirim?"))
        )
    }

    // 🔹 Çok sayıda hazır soru (14 adet)
    val quick: List<Pair<String, String>> = listOf(
        "Harcamalarımı göster" to "Son 30 günde toplam harcaman 12.450₺. En çok market (%32) ve ulaşım (%21). İstersen kategori grafiğini açarım.",
        "Yatırım önerileri"   to "Risk profilin orta. Öneri: %60 BIST30 endeks fonu, %20 TL mevduat, %20 altın fonu.",
        "Borçlarım nasıl?"    to "Toplam borç: 27.300₺. Kart 7.800₺ (son ödeme 25’i), ihtiyaç kredisi 19.500₺ (aylık 2.350₺).",
        "Birikim planı"       to "Acil durum için 45.000₺ hedefle. Aylık %20 tasarrufla ~7 ayda ulaşılır.",
        "Bütçe öner"          to "50/30/20 kuralı: 15.000₺ gelir → 7.500₺ ihtiyaç, 4.500₺ istek, 3.000₺ birikim/borç.",
        "Tasarruf ipuçları"   to "Market listesiyle alışveriş, nakit/ön ödemeli kart, istek harcamasına haftalık limit koy.",
        "Kart analizim"       to "Bu ay kart harcaması 9.120₺. Temassız %28, online %41. Taksitli harcama 2.300₺.",
        "Fatura özetim"       to "Aylık sabit giderlerin ~3.250₺: elektrik 820, su 260, doğalgaz 540, internet 320, gsm 180, kira 1.130.",
        "Hedef belirle"       to "6 ayda tatil için 30.000₺ biriktirme: aylık 5.000₺ otomatik ayırma öneririm.",
        "Açık kalem var mı?" to "Son 7 günde 3 bekleyen işlem var: Netflix 199₺, e‑Devlet 40₺, Yemeksepeti 156₺.",
        "Gelir analizi"       to "Son 3 ay net gelir ortalaması 15.800₺. Değişkenlik %6 (stabil).",
        "Vergi hatırlat"      to "Motorlu Taşıtlar 2. taksit son gün: 31 Temmuz. Ödeme planı istersen oluşturayım.",
        "Döviz/altın"         to "Portföy korunması için döviz/altın toplamının %20’yi geçmemesi önerilir.",
        "Risk profilim"       to "Kısa anketle risk profilini güncelleyebilirim. Son profilin: Orta (Skor 56/100)."
    )

    fun sendQuick(q: String, a: String) {
        messages = messages + ChatMsg(false, q) +
                ChatMsg(true, a + "\n\nOlmadıysa ‘Canlı desteğe bağla’ butonuna dokunabilirsin.")
    }

    val chipsState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChatAI") },
                actions = {
                    TextButton(onClick = onConnectLiveSupport) {
                        Icon(Icons.Outlined.HeadsetMic, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Canlı Destek")
                    }
                }
            )
        },
        bottomBar = {
            // 🔹 Yatay kaydırılabilir bar + sol/sağ oklar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = {
                        scope.launch {
                            val first = chipsState.firstVisibleItemIndex
                            chipsState.animateScrollToItem((first - 3).coerceAtLeast(0))
                        }
                    }
                ) { Icon(Icons.Outlined.ChevronLeft, contentDescription = "Geri") }

                LazyRow(
                    state = chipsState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(
                        items = quick,
                        key = { it.first }
                    ) { pair ->
                        val (q, a) = pair
                        SuggestionChip(
                            onClick = { sendQuick(q, a) },
                            label = { Text(q) }
                        )
                    }
                }

                FilledTonalIconButton(
                    onClick = {
                        scope.launch {
                            val last = chipsState.firstVisibleItemIndex + 5
                            chipsState.animateScrollToItem(last.coerceAtMost(quick.lastIndex))
                        }
                    }
                ) { Icon(Icons.Outlined.ChevronRight, contentDescription = "İleri") }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages) { m ->
                    val bg = if (m.fromBot) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.primaryContainer
                    val align = if (m.fromBot) Alignment.Start else Alignment.End
                    Surface(
                        color = bg,
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .wrapContentWidth(align)
                    ) {
                        Text(m.text, modifier = Modifier.padding(12.dp))
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = onConnectLiveSupport,
                    label = { Text("Cevap hoşuma gitmedi, canlı desteğe bağla") },
                    leadingIcon = { Icon(Icons.Outlined.ThumbDown, contentDescription = null) }
                )
            }
        }
    }
}
