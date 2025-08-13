package com.example.interchat.data

class ChatRepository(private val api: OpenAIService) {
    suspend fun ask(history: List<ChatMessage>): String {
        val resp = api.chat(ChatRequest(messages = history))
        return resp.choices.firstOrNull()?.message?.content ?: "Cevap alınamadı."
    }
}
