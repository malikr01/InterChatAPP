// app/src/main/java/com/example/interchat/ui/screens/ForgotPasswordScreen.kt
package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    var tc by remember { mutableStateOf("") }
    val canSend = tc.length == 11

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Şifre Sıfırla (Mock)") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Geri") } }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = tc,
                onValueChange = { tc = it.filter { c -> c.isDigit() }.take(11) },
                label = { Text("TC Kimlik No") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onDone,
                enabled = canSend,
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            ) { Text("Sıfırlama Bağlantısı Gönder") }

            if (!canSend) {
                Text(
                    "TC 11 haneli olmalı",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
