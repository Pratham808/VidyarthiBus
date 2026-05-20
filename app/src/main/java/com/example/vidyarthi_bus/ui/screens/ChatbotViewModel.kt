package com.example.vidyarthi_bus.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyarthi_bus.data.RouteRepository
import com.example.vidyarthi_bus.domain.model.CrowdLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val repository: RouteRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Hello! I'm your AI bus assistant. Ask me about route crowd status.", isUser = false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    fun sendMessage(text: String) {
        val current = _messages.value.toMutableList()
        current.add(ChatMessage(text, isUser = true))
        _messages.value = current

        viewModelScope.launch {
            _isTyping.value = true
            delay(1200)
            
            val routes = repository.getRoutes().first()
            val response = generateAIResponse(text, routes)
            
            val updated = _messages.value.toMutableList()
            updated.add(ChatMessage(response, isUser = false))
            _messages.value = updated
            _isTyping.value = false
        }
    }

    private fun generateAIResponse(query: String, routes: List<com.example.vidyarthi_bus.domain.model.Route>): String {
        return when {
            query.contains("crowd", ignoreCase = true) || query.contains("status", ignoreCase = true) -> {
                val fullCount = routes.count { it.lastCrowdLevel == CrowdLevel.FULL }
                if (fullCount > 0) {
                    "Currently, $fullCount routes are reported as FULL. I recommend checking alternative villages."
                } else {
                    "Great news! All monitored routes are currently reported as clear or half-full."
                }
            }
            query.contains("best time", ignoreCase = true) -> {
                "Historical data shows 8:15 AM is the optimal time to avoid the college rush."
            }
            query.contains("auto", ignoreCase = true) -> {
                "If your bus is full, I can help you find a Shared Auto. Check the 'Shared Auto' section on your dashboard."
            }
            else -> "I'm your transport AI. I can help with route status, peak timings, and shared auto alternatives!"
        }
    }
}