// app/src/main/java/com/example/interchat/MainActivity.kt
package com.example.interchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.interchat.data.di.AppConfig
import com.example.interchat.data.di.Repos
import com.example.interchat.data.session.AccountsStore
import com.example.interchat.data.session.UserSession
import com.example.interchat.ui.navigation.AppNav
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1) Uygulama açılır açılmaz mock kullanıcıya bağla (u1)
        if (UserSession.userId.value == null) {
            UserSession.setUserId("u1")
            Log.d("MainActivity", "Auto-connected to user: u1")
        }

        // 2) Bağlandıktan hemen sonra Hesaplar'ı repo'dan seed et → UI anında dolar
        lifecycleScope.launch {
            val uid = UserSession.userId.value ?: return@launch
            try {
                AccountsStore.onUserChanged(uid)
                val list = Repos.accountRepo.getAccounts(uid)   // mock ya da remote'a göre çalışır
                AccountsStore.seedIfEmpty(uid, list)            // zaten doluysa dokunmaz
                Log.d("MainActivity", "Seeded accounts: ${list.size}")
            } catch (t: Throwable) {
                Log.e("MainActivity", "Account seed error: ${t.message}", t)
            }
        }

        // Hangi modda çalıştığını logla (MOCK / REMOTE)
        val mode = if (AppConfig.API_BASE_URL.isNotBlank())
            "REMOTE (${AppConfig.API_BASE_URL})" else "MOCK"
        Log.d("MainActivity", "Mode = $mode")

        setContent { AppNav() }
    }
}
