package com.example.interchat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interchat.data.chat.ChatEvent
import com.example.interchat.data.chat.ChatMessage
import com.example.interchat.data.di.Repos
import com.example.interchat.domain.ErrorMapper
import com.example.interchat.util.JsonUtils
import com.example.interchat.ui.common.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ChatAIViewModel : ViewModel() {

    private val chatRepo = Repos.aiChatRepo

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents

    fun send(text: String) {
        val prompt = text.trim()
        if (prompt.isBlank()) return

        appendMessage(ChatMessage(id = uuid(), text = prompt, fromMe = true, isJson = JsonUtils.isJson(prompt)))

        val botId = uuid()
        appendMessage(ChatMessage(id = botId, text = "", fromMe = false, isJson = false))

        viewModelScope.launch {
            try {
                chatRepo.send(prompt).collect { event ->
                    when (event) {
                        is ChatEvent.Delta -> updateMessage(botId) { it.copy(text = it.text + event.text, isJson = false) }
                        is ChatEvent.Done  -> updateMessage(botId) { it.copy(text = event.fullText, isJson = JsonUtils.isJson(event.fullText)) }
                        is ChatEvent.Error -> handleError(botId, RuntimeException(event.message))
                    }
                }
            } catch (t: Throwable) {
                handleError(botId, t)
            }
        }
    }

    private fun handleError(botId: String, t: Throwable) {
        val friendly = ErrorMapper.toUserMessage(t)
        // Bot balonunda kibar metin
        updateMessage(botId) { it.copy(text = friendly, isJson = false) }
        // Snackbar
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar(friendly))
        }
    }

    private fun appendMessage(msg: ChatMessage) { _messages.update { it + msg } }

    private inline fun updateMessage(id: String, transform: (ChatMessage) -> ChatMessage) {
        _messages.update { list -> list.map { if (it.id == id) transform(it) else it } }
    }

    private fun uuid(): String = UUID.randomUUID().toString()
}
