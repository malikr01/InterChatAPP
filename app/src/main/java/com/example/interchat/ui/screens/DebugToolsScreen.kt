
package com.example.interchat.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.interchat.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugToolsScreen() {
    val ctx = LocalContext.current

    // Ekran açıldığında bir kez log
    LaunchedEffect(Unit) {
        if (BuildConfig.DEBUG) Log.d("DEBUG_MODE", "DebugToolsScreen açıldı")
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Debug Tools") }) }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ToolItem("Show test toast") {
                Toast.makeText(ctx, "Debug: hello 👋", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG_MODE", "Test toast gösterildi")
            }
            ToolItem("Simulate network error (mock)") {
                Toast.makeText(ctx, "Simulated 503", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG_MODE", "503 (mock) tetiklendi")
            }
            ToolItem("Reset demo session") {
                Toast.makeText(ctx, "Session reset (mock)", Toast.LENGTH_SHORT).show()
                Log.d("DEBUG_MODE", "Demo session reset (mock)")
            }
        }
    }
}

@Composable
private fun ToolItem(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(Modifier.padding(16.dp), contentAlignment = Alignment.CenterStart) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
