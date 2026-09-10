package com.messq.app.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messq.app.data.ChatMessage
import com.messq.app.data.groq.GroqMessage
import com.messq.app.data.groq.GroqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val groq = GroqRepository()

    val messages = mutableStateListOf(
        ChatMessage(
            text = "Hello! I am MessQ Assistant. I can help you with today's menu, crowd status, slot booking, and nutrition info. How can I help?",
            isUser = false
        )
    )

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(text = text, isUser = true))
        _isTyping.value = true
        viewModelScope.launch {
            val history = messages.map {
                GroqMessage(role = if (it.isUser) "user" else "assistant", content = it.text)
            }
            val reply = groq.chat(history)
            messages.add(ChatMessage(text = reply, isUser = false))
            _isTyping.value = false
        }
    }
}
