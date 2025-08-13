package com.example.interchat.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore("credentials")

class CredentialsStore(private val ctx: Context) {

    /** --- REGISTERED (kalıcı hesap) --- */
    private object REG {
        val TC   = stringPreferencesKey("reg_tc")
        val PASS = stringPreferencesKey("reg_pass")
    }

    /** --- REMEMBER ME (otomatik doldurma / autologin) --- */
    private object REM {
        val TC   = stringPreferencesKey("rem_tc")
        val PASS = stringPreferencesKey("rem_pass")
    }

    // Kayıtlı hesap yaz/oku
    suspend fun saveRegistered(tc: String, pass: String) {
        ctx.dataStore.edit { p ->
            p[REG.TC] = tc
            p[REG.PASS] = pass
        }
    }
    suspend fun getRegisteredOnce(): Pair<String?, String?> {
        val p = ctx.dataStore.data.first()
        return p[REG.TC] to p[REG.PASS]
    }

    // Beni hatırla yaz/oku/sil
    suspend fun saveRemember(tc: String, pass: String) {
        ctx.dataStore.edit { p ->
            p[REM.TC] = tc
            p[REM.PASS] = pass
        }
    }
    val remembered: Flow<Pair<String?, String?>> =
        ctx.dataStore.data.map { p -> p[REM.TC] to p[REM.PASS] }

    suspend fun clearRemember() {
        ctx.dataStore.edit { p ->
            p.remove(REM.TC)
            p.remove(REM.PASS)
        }
    }
}
