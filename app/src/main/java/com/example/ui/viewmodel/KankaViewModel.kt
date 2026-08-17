package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.SavedSnippet
import com.example.data.model.UserProfile
import com.example.data.repository.ChatRepository
import com.example.data.repository.ProfileRepository
import com.example.data.repository.SnippetRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class KankaViewModel(
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
    private val snippetRepository: SnippetRepository
) : ViewModel() {

    // User Profile
    val userProfile: StateFlow<UserProfile> = profileRepository.userProfile
        .flatMapLatest { profile ->
            flowOf(profile ?: UserProfile())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    // All Chat Sessions
    val sessions: StateFlow<List<ChatSession>> = chatRepository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Chat Session ID
    private val _currentSessionId = MutableStateFlow<Long?>(null)
    val currentSessionId: StateFlow<Long?> = _currentSessionId.asStateFlow()

    // Current Messages
    val currentMessages: StateFlow<List<ChatMessage>> = _currentSessionId
        .flatMapLatest { id ->
            if (id != null) {
                chatRepository.getMessagesForSession(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved Code Snippets
    val savedSnippets: StateFlow<List<SavedSnippet>> = snippetRepository.allSnippets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Loading / Thinking state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    init {
        // Initialize profile and default session
        viewModelScope.launch {
            profileRepository.getProfileDirect()
            chatRepository.allSessions.collect { list ->
                if (_currentSessionId.value == null && list.isNotEmpty()) {
                    _currentSessionId.value = list.first().sessionId
                } else if (list.isEmpty()) {
                    // Create initial welcome session
                    val newId = chatRepository.createNewSession("Kanka AI Sohbeti")
                    _currentSessionId.value = newId
                    val profile = profileRepository.getProfileDirect()
                    val hitap = profile.hitapSekli.ifBlank { "Kankam" }
                    val name = profile.name.ifBlank { "Dostum" }
                    val welcomeText = """
Selam $name! Hoş geldin $hitap! 🚀

Ben senin yapay zeka yazılımcı kankan **Kanka AI**. 

Birlikte neler yapabiliriz?
- 💻 **Karmaşık Yazılım Problemleri & Bug Çözümü**
- ⚡ **Sıfırdan Fonksiyon, Sınıf ve Mimari Kodları Üretme**
- 🧠 **Algoritma, LeetCode & Big-O Optimizasyonları**
- 🏗️ **Clean Architecture, MVVM & SOLID Tasarım Danışmanlığı**
- 💬 **Ve aklına gelen her türlü teknik ya da dostça sohbet!**

Aklındaki projeyi, çözemediğin o hatayı veya merak ettiğin herhangi bir konuyu hemen aşağıya yaz, birlikte halledelim $hitap! 🔥
                    """.trimIndent()
                    chatRepository.saveAiMessage(newId, welcomeText, modelUsed = "Kanka AI")
                }
            }
        }
    }

    fun selectSession(sessionId: Long) {
        _currentSessionId.value = sessionId
    }

    fun createNewSession(title: String = "Yeni Sohbet") {
        viewModelScope.launch {
            val newId = chatRepository.createNewSession(title)
            _currentSessionId.value = newId
            val profile = profileRepository.getProfileDirect()
            val hitap = profile.hitapSekli.ifBlank { "Kankam" }
            val welcomeText = "Selam $hitap! Yeni bir kodlama oturumu başlattık. Bugün ne geliştiriyoruz? 🚀"
            chatRepository.saveAiMessage(newId, welcomeText, modelUsed = "Kanka AI")
        }
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            chatRepository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                val remaining = sessions.value.filter { it.sessionId != sessionId }
                _currentSessionId.value = remaining.firstOrNull()?.sessionId
            }
        }
    }

    fun togglePinSession(sessionId: Long) {
        viewModelScope.launch {
            chatRepository.togglePinSession(sessionId)
        }
    }

    fun sendMessage(content: String) {
        val trimmed = content.trim()
        if (trimmed.isEmpty() || _isGenerating.value) return

        viewModelScope.launch {
            var activeId = _currentSessionId.value
            if (activeId == null) {
                activeId = chatRepository.createNewSession("Yeni Sohbet")
                _currentSessionId.value = activeId
            }

            // Save user message
            chatRepository.saveUserMessage(activeId, trimmed)

            _isGenerating.value = true
            try {
                val currentHistory = currentMessages.value
                val profile = userProfile.value
                val aiResponse = chatRepository.generateAiResponse(
                    sessionId = activeId,
                    userPrompt = trimmed,
                    pastMessages = currentHistory,
                    userProfile = profile
                )
                val modelName = if (profile.selectedModel.contains("pro")) "Pro Kod Ustası" else "Hızlı Flash"
                chatRepository.saveAiMessage(activeId, aiResponse, modelUsed = modelName)
            } catch (e: Exception) {
                chatRepository.saveAiMessage(
                    activeId,
                    "Kankam yanıt üretirken beklenmeyen bir durum oldu: ${e.message}",
                    isError = true
                )
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun sendDevToolPrompt(actionTitle: String, inputCodeOrQuery: String, language: String) {
        val prompt = when (actionTitle) {
            "Bug Fix" -> "Aşağıdaki $language kodundaki hatayı tespit et, neden kaynaklandığını açıkla ve %100 çalışan düzeltilmiş kodu ver:\n\n```$language\n$inputCodeOrQuery\n```"
            "Kod Üret" -> "Şu istek için $language dilinde en iyi pratiklerle (best practices), temiz ve çalışır kod yaz:\n\n$inputCodeOrQuery"
            "Algoritma Çöz" -> "Aşağıdaki algoritma problemini $language dilinde en optimize şekilde çöz, adım adım mantığını ve Zaman/Alan (Time/Space) karmaşıklığını açıkla:\n\n$inputCodeOrQuery"
            "Mimari & SOLID" -> "Şu senaryo için $language dilinde Clean Architecture, SOLID ve MVVM prensiplerine uygun mimari katmanları ve örnek kodları oluştur:\n\n$inputCodeOrQuery"
            "Optimizasyon" -> "Aşağıdaki $language kodunu performans, bellek ve okunabilirlik açısından refactor ve optimize et:\n\n```$language\n$inputCodeOrQuery\n```"
            "Birim Testi" -> "Aşağıdaki $language kodu için tüm sınır durumları (edge-cases) içeren kapsamlı birim testleri (Unit Tests) yaz:\n\n```$language\n$inputCodeOrQuery\n```"
            else -> inputCodeOrQuery
        }
        sendMessage(prompt)
    }

    fun updateUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            profileRepository.updateProfile(profile)
        }
    }

    fun saveSnippet(title: String, language: String, code: String, note: String = "") {
        viewModelScope.launch {
            snippetRepository.saveSnippet(title, language, code, note)
        }
    }

    fun deleteSnippet(snippetId: Long) {
        viewModelScope.launch {
            snippetRepository.deleteSnippet(snippetId)
        }
    }
}

class KankaViewModelFactory(
    private val chatRepository: ChatRepository,
    private val profileRepository: ProfileRepository,
    private val snippetRepository: SnippetRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KankaViewModel::class.java)) {
            return KankaViewModel(chatRepository, profileRepository, snippetRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
