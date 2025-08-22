package com.example.interchat.data.chat

import kotlinx.coroutines.flow.Flow

/** AI sohbet kaynakları için ortak arayüz (Mock / Backend) */
interface ChatRepository {
    /** prompt gönderir, streaming event'leri döner (Delta/Done/Error) */
    fun send(prompt: String): Flow<ChatEvent>
}

/** Streaming olayları */
sealed class ChatEvent {
    data class Delta(val text: String) : ChatEvent()
    data class Done(val fullText: String) : ChatEvent()
    data class Error(val message: String) : ChatEvent()
}
