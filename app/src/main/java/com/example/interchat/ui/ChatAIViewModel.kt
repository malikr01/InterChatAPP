package com.example.interchat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interchat.BuildConfig
import com.example.interchat.data.ChatMessage
import com.example.interchat.data.ChatRepository
import com.example.interchat.data.OpenAIService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatUiMessage(val fromMe: Boolean, val text: String)

class ChatAIViewModel : ViewModel() {

    private val service by lazy { OpenAIService.create(BuildConfig.OPENAI_API_KEY) }
    private val repo by lazy { ChatRepository(service) }

    private val _messages = MutableStateFlow<List<ChatUiMessage>>(emptyList())
    val messages: StateFlow<List<ChatUiMessage>> = _messages

    private val history = mutableListOf(
        ChatMessage("system", "You are a helpful assistant that replies concisely in Turkish.")
    )

    fun send(text: String) {
        if (text.isBlank()) return
        _messages.value = _messages.value + ChatUiMessage(true, text)

        viewModelScope.launch {
            try {
                history += ChatMessage("user", text)
                _messages.value = _messages.value + ChatUiMessage(false, "Yazıyor…")
                val answer = repo.ask(history)
                _messages.value = _messages.value.dropLast(1) + ChatUiMessage(false, answer)
                history += ChatMessage("assistant", answer)
            } catch (e: Exception) {
                _messages.value = _messages.value.dropLast(1) + ChatUiMessage(false, "Hata: ${e.message}")
            }
        }
    }
}
