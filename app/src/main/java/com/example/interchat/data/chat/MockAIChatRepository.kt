package com.example.interchat.data.chat

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * AI servisi hazır değilken kullanılacak yalın metin odaklı mock.
 * Kullanıcıdan “json” yazmasını istemez; her zaman okunabilir metin üretir.
 */
class MockAIChatRepository : ChatRepository {

    override fun send(prompt: String): Flow<ChatEvent> = flow {
        val p = prompt.lowercase()

        val reply: String = when {
            listOf("tasarruf", "tasarruf ipucu", "birikim").any { it in p } -> """
                İşte hızlı tasarruf önerileri:
                • Market alışverişinde marka yerine muadil ürünleri deneyin.
                • Ulaşımda toplu taşımayı haftada en az 2 gün tercih edin.
                • Otomatik birikim talimatıyla maaş gününde %3 kenara ayırın.
                • Dijital aboneliklerinizi gözden geçirip kullanmadıklarınızı iptal edin.
            """.trimIndent()

            listOf("özet", "ozet", "bütçe", "butce").any { it in p } -> """
                Son ay bütçe özeti:
                • Toplam gelir: 17.500 ₺
                • Toplam gider: 6.430 ₺
                • Net: 11.070 ₺
                • Gıda ve ulaşım harcamaları netin %26’sı; bu kalemlerde %10 azaltım hedefleyin.
            """.trimIndent()

            listOf("harcama", "kart", "pos", "işlem", "islem").any { it in p } -> """
                Kart harcama analizi:
                • En sık harcama kategorisi: Gıda (%22)
                • Haftanın zirvesi: Cumartesi
                • Ay ortasına göre kalan günlük ortalama bütçe: 430 ₺
                • Nakit geri kazanımı için markette X kart kampanyasını değerlendirebilirsiniz.
            """.trimIndent()

            listOf("merhaba", "selam", "hello", "hi").any { it in p } -> """
                Merhaba! Finans sorularını yanıtlayabilirim.
                Örn: “Bütçemi özetler misin?”, “Tasarruf ipuçları ver”, “Kart harcamalarım nasıl?”
            """.trimIndent()

            else -> """
                Anladım. Bu konuyu biraz açar mısın?
                Bütçe özeti, tasarruf önerisi, kart harcama analizi gibi başlıklarda yardımcı olabilirim.
            """.trimIndent()
        }

        // Küçük küçük parçalar (streaming hissi)
        reply.chunked(18).forEach {
            emit(ChatEvent.Delta(it))
            delay(35)
        }
        emit(ChatEvent.Done(reply))
    }
}
