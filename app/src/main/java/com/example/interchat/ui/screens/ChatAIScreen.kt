@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.interchat.ui.ChatAIViewModel
import com.example.interchat.ui.components.JsonViewer
import com.example.interchat.ui.common.UiEvent
import kotlinx.coroutines.launch

@Composable
fun ChatAIScreen(
    onConnectLiveSupport: () -> Unit = {}
) {
    val vm: ChatAIViewModel = viewModel()
    val scope = rememberCoroutineScope()

    val messages by vm.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    val quick = listOf(
        "Bütçemi özetler misin?",
        "Tasarruf ipuçları ver",
        "Kart harcamalarım nasıl?",
        "Yaklaşan ödemelerim var mı?",
        "Bu ay hedefe göre durumum?"
    )

    val chipsState = rememberLazyListState()
    val listState  = rememberLazyListState()
    val snackbar   = remember { SnackbarHostState() }

    // UI Event -> Snackbar
    LaunchedEffect(Unit) {
        vm.uiEvents.collect { ev ->
            when (ev) {
                is UiEvent.ShowSnackbar -> snackbar.showSnackbar(message = ev.message, actionLabel = ev.actionLabel)
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

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
        snackbarHost = { SnackbarHost(snackbar) },   // <— eklendi
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
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
                        items(quick) { q ->
                            SuggestionChip(
                                onClick = { vm.send(q) },
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

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Sorunu yaz…") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                val text = input.trim()
                                if (text.isNotEmpty()) { vm.send(text); input = "" }
                            }
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    FilledIconButton(onClick = {
                        val text = input.trim()
                        if (text.isNotEmpty()) { vm.send(text); input = "" }
                    }) { Icon(Icons.Outlined.Send, contentDescription = "Gönder") }
                }
            }
        }
    ) { pad ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { m ->
                val align = if (m.fromMe) Alignment.End else Alignment.Start
                val bg = if (m.fromMe) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = if (m.fromMe) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        color = bg,
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentWidth(align)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            if (m.isJson) JsonViewer(jsonText = m.text, initiallyExpanded = true)
                            else Text(m.text)
                        }
                    }
                }
            }
        }
    }
}
