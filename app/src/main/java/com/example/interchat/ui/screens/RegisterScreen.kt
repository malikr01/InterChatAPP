// app/src/main/java/com/example/interchat/ui/screens/RegisterScreen.kt
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegistered: (tc: String, pass: String) -> Unit   // <- şifre de dışarı veriliyor
) {
    var tc by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }

    // basit kontroller
    val tcOk = tc.length == 11
    val passOk = pass.length >= 4 && pass == pass2
    val canSubmit = tcOk && passOk

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kayıt Ol (Mock)") },
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

            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Şifre (min 4)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            )

            OutlinedTextField(
                value = pass2,
                onValueChange = { pass2 = it },
                label = { Text("Şifre Tekrar") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
            )

            Button(
                onClick = { onRegistered(tc, pass) },   // <- tc + şifre gönder
                enabled = canSubmit,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
            ) { Text("Kayıt Ol") }

            if (!tcOk) {
                Text(
                    "TC 11 haneli olmalı",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else if (!passOk) {
                Text(
                    "Şifreler uyuşmalı ve en az 4 karakter olmalı",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
