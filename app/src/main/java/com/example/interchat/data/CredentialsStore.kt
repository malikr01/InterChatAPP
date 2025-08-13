// app/src/main/java/com/example/interchat/data/CredentialsStore.kt
package com.example.interchat.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "credentials_prefs")

class CredentialsStore(private val context: Context) {

    companion object {
        private val KEY_TC = stringPreferencesKey("tc")
        private val KEY_PASS = stringPreferencesKey("pass") // sadece bu isim
    }

    suspend fun saveCredentials(tc: String, password: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TC] = tc
            prefs[KEY_PASS] = password
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    val credentials: Flow<Pair<String?, String?>> =
        context.dataStore.data.map { prefs ->
            Pair(prefs[KEY_TC], prefs[KEY_PASS])
        }

    val tc: Flow<String?> = context.dataStore.data.map { it[KEY_TC] }
    val pass: Flow<String?> = context.dataStore.data.map { it[KEY_PASS] }
}
