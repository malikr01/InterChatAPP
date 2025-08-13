package com.example.interchat.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun HomeRootScreen( // ← adı HomeRootScreen
    onOpenAccountDetail: (String) -> Unit = {},
    onOpenCardDetail: () -> Unit = {}
) {
    data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
    val tabs = listOf(
        Tab("chat", "Chat", Icons.Filled.Chat),
        Tab("accounts", "Hesaplar", Icons.Filled.AccountCircle),
        Tab("transactions", "İşlemler", Icons.Filled.SwapHoriz),
        Tab("faq", "SSS", Icons.Filled.HelpOutline),
    )
    var current by rememberSaveable { mutableStateOf("chat") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { t ->
                    NavigationBarItem(
                        selected = current == t.route,
                        onClick = { current = t.route },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { p ->
        when (current) {
            "chat" -> Surface(Modifier.padding(p)) { Text("Chat (yakında)") }
            "accounts" -> AccountsScreen(
                onAccountClick = { id -> onOpenAccountDetail(id) },
                onOpenCardDetail = { onOpenCardDetail() }
            )
            "transactions" -> Surface(Modifier.padding(p)) { Text("İşlemler (yakında)") }
            "faq" -> Surface(Modifier.padding(p)) { FaqScreen() }
        }
    }
}
