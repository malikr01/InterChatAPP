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
    FaqItem("Hesap açma veya kapatma işlemini nasıl yapabilirim?",
        "Uygulama üzerinden “Hesap İşlemleri” menüsüne giderek yeni hesap açabilir veya mevcut hesabınızı kapatma talebi oluşturabilirsiniz."),
    FaqItem("Kredi kartı borcumu nasıl görüntüler ve öderim?",
        "“Kart Detay” ekranında borcunuzu görebilir, “Borç Öde” butonunu kullanarak ödeme yapabilirsiniz."),
    FaqItem("Şifremi unuttum, ne yapmalıyım?",
        "Giriş ekranındaki ‘Şifremi Unuttum’ bağlantısından yeni şifre oluşturabilirsiniz."),
    FaqItem("IBAN’ımı nereden görürüm?",
        "Hesaplar sekmesinde ilgili hesabın ‘Hesap Detayı’ ekranında IBAN görüntülenir."),
    FaqItem("İşlem geçmişimi nasıl görürüm?",
        "‘İşlemler’ sekmesinden son hareketlerinizi listeleyebilirsiniz.")

)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen() {
    var selected by remember { mutableStateOf<FaqItem?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Sıkça Sorulan Sorular") })
        }
    ) { p ->
        LazyColumn(
            modifier = Modifier
                .padding(p)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(faqList) { faq ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected = faq }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(faq.question, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text("Cevabı görmek için tıklayın", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }

        // Küçük pencere (AlertDialog)
        if (selected != null) {
            AlertDialog(
                onDismissRequest = { selected = null },
                title = { Text(selected!!.question) },
                text = { Text(selected!!.answer) },
                confirmButton = {
                    TextButton(onClick = { selected = null }) {
                        Text("Tamam")
                    }
                }
            )
        }
    }
}
