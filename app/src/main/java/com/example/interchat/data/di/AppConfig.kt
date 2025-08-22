package com.example.interchat.data.di

/**
 * Ortak konfigürasyonlar.
 * Finans backend için kullandığın alanları korudum; AI için yenileri eklendi.
 */
object AppConfig {

    // --- Finans backend (mevcutta kullandığın) ---
    // Boş değilse finans reposu remote'a döner, boşsa mock çalışır.
    const val API_BASE_URL: String = "" // örn: "https://api.bankam.com"

    // --- AI (Chat) tarafı ---
    enum class AISource { Mock, Backend }
    var AI_SOURCE: AISource = AISource.Mock

    // Backend AI base URL (SSE/REST)
    const val AI_BASE_URL: String = "" // örn: "https://mini-api.example.com"

    // Kimlik doğrulama modu
    enum class AIAuthMode { None, Bearer, ApiKey }
    var AI_AUTH_MODE: AIAuthMode = AIAuthMode.None

    // Bearer kullanacaksan (statik token)
    const val AI_STATIC_BEARER: String = "" // örn: "eyJhbGciOiJI..."

    // API Key kullanacaksan
    const val AI_API_KEY_HEADER: String = "X-API-Key" // backend'in istediği header adı
    const val AI_API_KEY: String = ""                 // key değeri
}
