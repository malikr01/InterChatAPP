package com.example.interchat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*

data class Account(
    val id: String,
    val title: String,
    val iban: String,
    val currency: Currency,
    val balanceMinor: Long
)

private fun money(minor: Long, cur: Currency): String =
    NumberFormat.getCurrencyInstance(Locale("tr","TR")).apply { currency = cur }
        .format(minor / 100.0)

private const val USER_NAME = "Kullanıcı Adı"
private const val CARD_MASKED = "5324 •••• •••• 2741"

private val demoAccounts = listOf(
    Account("1","Vadesiz TL","TR12 1234 5678 9012 3456 78", Currency.getInstance("TRY"), 1_284_500),
    Account("2","Vadeli TL","TR90 0001 2345 6789 0000 11", Currency.getInstance("TRY"), 15_400_000)
)

@Composable
fun AccountsScreen(
    onAccountClick: (String) -> Unit,
    onOpenCardDetail: () -> Unit = {}
) {
    Scaffold { p ->
        LazyColumn(
            modifier = Modifier
                .padding(p)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(Modifier.clickable { onOpenCardDetail() }) {
                    CreditCardHeader(
                        name = USER_NAME,
                        maskedCard = CARD_MASKED,
                        iban = demoAccounts.first().iban,
                        balance = money(demoAccounts.first().balanceMinor, demoAccounts.first().currency)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("Hesaplar", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 4.dp, bottom = 4.dp))
            }
            items(demoAccounts) { acc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAccountClick(acc.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(acc.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(6.dp))
                        Text("IBAN: ${acc.iban}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(6.dp))
                        Text("Bakiye: ${money(acc.balanceMinor, acc.currency)}")
                    }
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun CreditCardHeader(name: String, maskedCard: String, iban: String, balance: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(maskedCard, color = Color.White, fontSize = 16.sp)
            Column {
                Text("IBAN: $iban", color = Color.White, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text("Bakiye: $balance", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
