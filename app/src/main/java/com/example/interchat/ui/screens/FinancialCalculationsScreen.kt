package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height


// ✅ Esas (parametreli) sürüm — AppNav burayı çağırmalı
@Composable
fun FinancialCalculationsScreen(
    onLoanCalcClick: () -> Unit,
    onPlanClick: () -> Unit,
    onInvestClick: () -> Unit,
    onFxClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Finansal Hesaplamalar", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLoanCalcClick, modifier = Modifier.fillMaxWidth()) {
            Text("Faiz Hesaplamaları (Kredi)")
        }
        Button(onClick = onPlanClick, modifier = Modifier.fillMaxWidth()) {
            Text("Kredi Ödeme Planı ve Maliyet Hesapları")
        }
        Button(onClick = onInvestClick, modifier = Modifier.fillMaxWidth()) {
            Text("Yatırım Getirisi ve Portföy Simülasyonları")
        }
        Button(onClick = onFxClick, modifier = Modifier.fillMaxWidth()) {
            Text("Döviz ve Kur Hesaplamaları")
        }
    }
}

// ✅ Geriye dönük uyumluluk için parametresiz sarmalayıcı (AppNav dışındaki eski çağrılar bozulmasın)
@Composable
fun FinancialCalculationsScreen() {
    FinancialCalculationsScreen(
        onLoanCalcClick = {},
        onPlanClick = {},
        onInvestClick = {},
        onFxClick = {}
    )
}
