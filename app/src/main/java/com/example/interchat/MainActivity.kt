package com.example.interchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.interchat.ui.navigation.AppRoot
// Eğer kendi temanız varsa (ui/theme altında) onu import edin:
// import com.example.interchat.ui.theme.InterChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Kendi temanız varsa InterChatTheme { ... } kullanın
            // InterChatTheme {
            MaterialTheme {
                Surface {
                    AppRoot()   // ⬅️ bizim navigation + bottom bar buradan başlıyor
                }
            }
            // }
        }
    }
}
