package com.example.interchat.ui.screens

// ==== IMPORTS: Tüm importlar package'dan hemen sonra olmalı ====
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Renk sabitleri (Color.kt). Paket adın bu ise import böyle olmalı:
import com.example.interchat.ui.theme.*

data class FaqItem(val question: String, val answer: String)

// Popüler başlıklar
private val popularList = listOf(
    "FinansAI nedir ve nasıl çalışır?",
    "Verilerim güvende mi?",
    "Hangi bankalarla entegre çalışıyor?"
)

// SSS içeriği
private val faqList = listOf(
    FaqItem("FinansAI nedir ve nasıl çalışır?", "Gelir/gider ve işlem verilerine göre kişiselleştirilmiş öneriler sunar."),
    FaqItem("Verilerim güvende mi?", "Tüm veriler TLS ile şifrelenir; KVKK ve regülasyonlara uyumludur."),
    FaqItem("Hangi bankalarla entegre çalışıyor?", "Hesaplar > Banka Ekle ekranından güncel listeyi görebilirsiniz."),
    FaqItem("Çevrimdışı kullanabilir miyim?", "Bazı ekranlar çevrimdışı çalışır; bakiye/işlem için internet gerekir."),
    FaqItem("AI asistan nasıl çalışıyor?", "Sorunuzu yazın/konuşun; anında yanıt alın."),
    FaqItem("Biyometrik güvenlik nasıl ayarlanır?", "Profil > Güvenlik > Biyometri."),
    FaqItem("Push bildirimlerini nasıl yönetebilirim?", "Profil > Bildirimler."),
    FaqItem("Hesabımı nasıl silebilirim?", "Profil > Hesap İşlemleri > Hesabı Sil."),
    FaqItem("Otomatik kategorilendirme nasıl çalışır?", "Harcamalar açıklama, işyeri ve tutara göre sınıflandırılır."),
    FaqItem("Destek ekibiyle nasıl iletişime geçebilirim?", "SSS > Sorunu Sor kartından veya Profil > Destek."),
    // Senin eklediklerin:
    FaqItem("Kart borcumu nereden öderim?", "İşlemler > Fatura Öde menüsünden."),
    FaqItem("Kredi faiz hesaplamalarını nereden yapabilirim?", "İşlemler > Hesaplamalar > Faiz hesaplamaları."),
    FaqItem("IBAN’ımı nereden görürüm?", "Hesaplar > Hesap Detayı ekranında."),
    FaqItem("Şifremi unuttum, ne yapmalıyım?", "Giriş > ‘Şifremi Unuttum’ bağlantısından.")
)

@Composable
fun FaqScreen(
    onStartChat: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    // NOTE: SnapshotStateMap save edilemez; rememberSaveable kullanma
    val expanded = remember { mutableStateMapOf<String, Boolean>() }

    val filtered = remember(query) {
        if (query.isBlank()) faqList
        else faqList.filter {
            it.question.contains(query, true) || it.answer.contains(query, true)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onStartChat, containerColor = PrimaryDark) {
                Icon(Icons.Outlined.Chat, contentDescription = "InterChat")
            }
        }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Üst banner + arama (gradient)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(
                            Brush.linearGradient(
                                listOf(GradientIntertech2Start, GradientIntertech2End)
                            )
                        )
                        .padding(vertical = 18.dp, horizontal = 16.dp)
                ) {
                    Column {
                        Text(
                            "Nasıl yardımcı olabiliriz?",
                            color = SurfaceLight,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 30.sp
                            )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Sık sorulan soruları sizin için derledik",
                            color = SurfaceLight.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                            placeholder = { Text("Soru ara…") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Popüler sorular kartı
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "Popüler Sorular",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.height(8.dp))
                        popularList.forEach { title ->
                            ListItem(
                                leadingContent = { Icon(Icons.Outlined.Star, null, tint = PrimaryDark) },
                                headlineContent = {
                                    Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                },
                                trailingContent = { Icon(Icons.Outlined.ExpandMore, null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        faqList.firstOrNull { it.question.contains(title, true) }?.let {
                                            expanded[it.question] = !(expanded[it.question] ?: false)
                                        }
                                    }
                            )
                            Divider()
                        }
                    }
                }
            }

            // “Sorunu Sor” kartı
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(GradientIntertech3Start, GradientIntertech3End)
                                )
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = SurfaceLight.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Chat, null, tint = SurfaceLight)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Özel sorunuz mu var?", color = SurfaceLight, fontWeight = FontWeight.SemiBold)
                            Text("AI asistanım ile anında cevap alın!", color = SurfaceLight.copy(alpha = 0.9f))
                        }
                        Button(
                            onClick = onStartChat,
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceLight)
                        ) { Text("InterChat Başlat", color = OnSurfaceLight) }
                    }
                }
            }

            // Başlık
            item {
                Text("Sık Sorulan Sorular", style = MaterialTheme.typography.titleLarge)
            }

            // Akordeon liste
            items(filtered, key = { it.question }) { faq ->
                val isOpen = expanded[faq.question] ?: false
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { expanded[faq.question] = !isOpen }
                        .padding(vertical = 6.dp)
                ) {
                    ListItem(
                        leadingContent = { Icon(Icons.Outlined.Star, null, tint = PrimaryDark) },
                        headlineContent = { Text(faq.question, style = MaterialTheme.typography.titleMedium) },
                        trailingContent = {
                            Icon(
                                if (isOpen) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                contentDescription = null
                            )
                        }
                    )
                    AnimatedVisibility(visible = isOpen) {
                        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            Text(faq.answer, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    Divider()
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}
