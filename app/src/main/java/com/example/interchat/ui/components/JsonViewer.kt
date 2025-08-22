@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.interchat.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.interchat.util.JsonUtils

/**
 * JSON metnini pretty-print + collapse ile gösterir.
 */
@Composable
fun JsonViewer(
    jsonText: String,
    initiallyExpanded: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }
    val ctx = LocalContext.current
    val pretty = remember(jsonText) { JsonUtils.prettify(jsonText) }

    ElevatedCard(modifier = modifier) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("JSON", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { copyToClipboard(ctx, pretty) }) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = "Kopyala")
            }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = if (expanded) "Daralt" else "Genişlet"
                )
            }
        }
        if (expanded) {
            Divider()
            Text(
                pretty,
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun copyToClipboard(ctx: Context, text: String) {
    val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("json", text))
    // İstersen Snackbar tetikleyebilirsin; sade bıraktım.
}
