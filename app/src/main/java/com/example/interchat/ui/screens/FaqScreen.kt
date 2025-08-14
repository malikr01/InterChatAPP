package com.example.interchat.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class FaqItem(val question: String, val answer: String)

private val faqList = listOf(
    FaqItem("Kart borcumu nereden öderim?", "İşlemler > Fatura Öde menüsünden."),
    FaqItem("Hesap hareketleri nerede?", "Kişisel Bilgi > Hesap Hareketleri ekranında."),
    FaqItem("IBAN’ımı nereden görürüm?", "Hesaplar > Hesap Detayı ekranında IBAN yer alır."),
    FaqItem("Şifremi unuttum, ne yapmalıyım?", "Giriş ekranındaki ‘Şifremi Unuttum’ bağlantısından yeni şifre oluşturabilirsin.")
)

@Composable
fun FaqScreen() {
    var selected by remember { mutableStateOf<FaqItem?>(null) }

    Scaffold { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Sıkça Sorulan Sorular",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(Modifier.height(8.dp))
            }

            items(faqList) { faq ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = faq }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(faq.question, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        //Text("Cevabı görmek için tıklayın", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    // Küçük pencere (AlertDialog)
    if (selected != null) {
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text(selected!!.question) },
            text = { Text(selected!!.answer) },
            confirmButton = {
                TextButton(onClick = { selected = null }) { Text("Tamam") }
            }
        )
    }
}
