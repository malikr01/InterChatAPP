// app/src/main/java/com/example/interchat/MainActivity.kt
package com.example.interchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.interchat.ui.navigation.AppNav
import com.example.interchat.BuildConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Terminal/Logcat için basit DEBUG bilgisi
        if (BuildConfig.DEBUG) {
            Log.d("DEBUG_MODE", "Uygulama DEBUG modda çalışıyor ✅")
        } else {
            Log.d("DEBUG_MODE", "Uygulama RELEASE modda çalışıyor 🚀")
        }

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
