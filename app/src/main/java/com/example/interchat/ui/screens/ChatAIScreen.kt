package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interchat.BuildConfig
import com.example.interchat.ui.ChatAIViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAIScreen(vm: ChatAIViewModel = viewModel()) {
    var input by remember { mutableStateOf("") }
    val messages by vm.messages.collectAsState()   // ✅ ismi "messages" yaptık

    Scaffold(
        topBar = { TopAppBar(title = { Text("ChatAI") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->                 // ✅ fonksiyonla çakışmıyor
                    val align = if (msg.fromMe)
                        Alignment.CenterEnd              // ✅ Box Alignment
                    else
                        Alignment.CenterStart

                    val bg = if (msg.fromMe)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant

                    val fg = if (msg.fromMe)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Box(Modifier.fillMaxWidth(), contentAlignment = align) {
                        Surface(color = bg, shape = MaterialTheme.shapes.medium) {
                            Text(
                                msg.text,
                                color = fg,
                                modifier = Modifier.padding(10.dp).widthIn(max = 320.dp)
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Mesaj yaz…") },
                    singleLine = true
                )
                Button(
                    onClick = {
                        val text = input.trim()
                        if (text.isNotBlank()) {
                            vm.send(text)      // ✅ gerçek API çağrısı
                            input = ""
                        }
                    },
                    enabled = input.isNotBlank()
                ) { Text("Gönder") }
            }

            if (BuildConfig.OPENAI_API_KEY.isBlank()) {
                Text(
                    "UYARI: OPENAI_API_KEY boş. local.properties içine ekleyin ve Sync yapın.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }
        }
    }
}
