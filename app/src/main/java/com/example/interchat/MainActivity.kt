package com.example.interchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.interchat.ui.navigation.AppNav

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Eğer kendi temanız varsa burada kullanın (InterChatTheme vs.)
            MaterialTheme {
                Surface {
                    AppNav()   // ✅ navigation + bottom bar buradan başlıyor
                }
            }
        }
    }
}
