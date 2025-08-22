package com.example.interchat.data.chat

import java.time.Instant

/** Chat/AI ekranında gösterilen mesaj modeli */
data class ChatMessage(
    val id: String,
    val text: String,
    val fromMe: Boolean,
    val createdAt: Instant = Instant.now(),
    val isJson: Boolean = false
)
