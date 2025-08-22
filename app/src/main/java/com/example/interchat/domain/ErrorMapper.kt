package com.example.interchat.domain

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

/**
 * Hangi Exception gelirse gelsin, kullanıcıya okunabilir bir mesaj döndürür.
 * Backend/AI tarafı değişse bile bu sınıf sabit kalır.
 */
object ErrorMapper {

    fun toUserMessage(error: Throwable?): String {
        if (error == null) return generic()

        return when (error) {
            is UnknownHostException -> "İnternet bağlantısı yok gibi görünüyor. Lütfen bağlantını kontrol edip tekrar dener misin?"
            is SocketTimeoutException -> "Sunucudan yanıt alınamadı. Birazdan tekrar dener misin?"
            is SSLHandshakeException -> "Güvenli bağlantı kurulamadı. Tarih/saati ve ağ ayarlarını kontrol eder misin?"
            else -> {
                // Metin bazlı ipuçları (repo ErrorEvent.message olarak string yollarsa)
                val m = error.message?.lowercase().orEmpty()
                when {
                    "rate" in m && "limit" in m -> "Bugün çok fazla istek yaptık. Bir süre sonra tekrar dener misin?"
                    "unauthorized" in m || "401" in m -> "Oturum doğrulaması gerekli. Giriş yapıp tekrar dener misin?"
                    "forbidden" in m || "403" in m -> "Bu işlem için yetkin yok. Farklı bir hesapla denemeyi düşünebilirsin."
                    "not found" in m || "404" in m -> "Aradığın bilgiye ulaşamadım. Alanı/kimliği kontrol edip tekrar dener misin?"
                    "timeout" in m -> "Sunucu geç yanıt verdi. Birazdan tekrar dener misin?"
                    else -> generic()
                }
            }
        }
    }

    private fun generic() =
        "Bir şeyler ters gitti. Tekrar dener misin? Sorun devam ederse bize haber ver."
}
