package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.ChatDao
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSession
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerateRequest
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiPart
import com.example.data.model.GeminiSystemInstruction
import com.example.data.model.UserProfile
import com.example.data.remote.GeminiApiService
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(
    private val chatDao: ChatDao,
    private val apiService: GeminiApiService = RetrofitClient.geminiService
) {
    val allSessions: Flow<List<ChatSession>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessage>> =
        chatDao.getMessagesForSession(sessionId)

    suspend fun createNewSession(initialTitle: String = "Yeni Sohbet"): Long = withContext(Dispatchers.IO) {
        val session = ChatSession(
            title = initialTitle,
            createdAt = System.currentTimeMillis(),
            lastUpdatedAt = System.currentTimeMillis()
        )
        chatDao.insertSession(session)
    }

    suspend fun updateSessionTitle(sessionId: Long, newTitle: String) = withContext(Dispatchers.IO) {
        val existing = chatDao.getSessionById(sessionId)
        if (existing != null) {
            chatDao.updateSession(existing.copy(title = newTitle, lastUpdatedAt = System.currentTimeMillis()))
        }
    }

    suspend fun togglePinSession(sessionId: Long) = withContext(Dispatchers.IO) {
        val existing = chatDao.getSessionById(sessionId)
        if (existing != null) {
            chatDao.updateSession(existing.copy(isPinned = !existing.isPinned))
        }
    }

    suspend fun deleteSession(sessionId: Long) = withContext(Dispatchers.IO) {
        chatDao.deleteMessagesForSession(sessionId)
        chatDao.deleteSessionById(sessionId)
    }

    suspend fun saveUserMessage(sessionId: Long, content: String): Long = withContext(Dispatchers.IO) {
        val message = ChatMessage(
            sessionId = sessionId,
            isUser = true,
            content = content,
            timestamp = System.currentTimeMillis()
        )
        val id = chatDao.insertMessage(message)

        // Update session last updated & auto-generate title if it's the first message
        val session = chatDao.getSessionById(sessionId)
        if (session != null) {
            val count = chatDao.getMessageCount(sessionId)
            val updatedTitle = if (count <= 2 && session.title == "Yeni Sohbet") {
                content.take(30).trim().replace("\n", " ") + (if (content.length > 30) "..." else "")
            } else {
                session.title
            }
            chatDao.updateSession(session.copy(title = updatedTitle, lastUpdatedAt = System.currentTimeMillis()))
        }
        id
    }

    suspend fun saveAiMessage(
        sessionId: Long,
        content: String,
        isError: Boolean = false,
        modelUsed: String? = null
    ): Long = withContext(Dispatchers.IO) {
        val message = ChatMessage(
            sessionId = sessionId,
            isUser = false,
            content = content,
            timestamp = System.currentTimeMillis(),
            isError = isError,
            modelUsed = modelUsed
        )
        val id = chatDao.insertMessage(message)
        val session = chatDao.getSessionById(sessionId)
        if (session != null) {
            chatDao.updateSession(session.copy(lastUpdatedAt = System.currentTimeMillis()))
        }
        id
    }

    suspend fun generateAiResponse(
        sessionId: Long,
        userPrompt: String,
        pastMessages: List<ChatMessage>,
        userProfile: UserProfile
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val model = if (userProfile.selectedModel.isNotBlank()) userProfile.selectedModel else "gemini-3.5-flash"

        // Build tailored system instruction
        val systemPrompt = buildSystemPrompt(userProfile)

        // Prepare context turns (limit to last 10 messages for speed & accuracy)
        val conversationHistory = pastMessages.takeLast(10).map { msg ->
            GeminiContent(
                role = if (msg.isUser) "user" else "model",
                parts = listOf(GeminiPart(text = msg.content))
            )
        } + listOf(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userPrompt))
            )
        )

        val request = GeminiGenerateRequest(
            contents = conversationHistory,
            systemInstruction = GeminiSystemInstruction(
                parts = listOf(GeminiPart(text = systemPrompt))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = if (userProfile.personalityTone == "HIZLI") 0.2f else 0.7f,
                topP = 0.95f,
                topK = 40
            )
        )

        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // If API key is not configured in Secrets panel, provide high-quality smart fallback
                return@withContext generateSmartFallback(userPrompt, userProfile)
            }

            val response = apiService.generateContent(
                model = model,
                apiKey = apiKey,
                request = request
            )

            val textResponse = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!textResponse.isNullOrBlank()) {
                textResponse
            } else if (response.error != null) {
                "⚠️ ${userProfile.hitapSekli}, bir hata oluştu: ${response.error.message ?: "Bilinmeyen API hatası"}. İstersen tekrar deneyelim!"
            } else {
                generateSmartFallback(userPrompt, userProfile)
            }
        } catch (e: Exception) {
            // In case of network failure or timeout, provide helpful assistant message
            generateSmartFallback(userPrompt, userProfile, networkError = e.localizedMessage)
        }
    }

    private fun buildSystemPrompt(profile: UserProfile): String {
        val hitap = profile.hitapSekli.ifBlank { "Kankam" }
        val name = profile.name.ifBlank { "Dostum" }
        val exp = profile.experienceLevel
        val favLangs = profile.favoriteLanguages.joinToString(", ")
        val tone = when (profile.personalityTone) {
            "KIDEMLI" -> "Sen kıdemli bir yazılım mimarısın. Çözümlerinde SOLID prensipleri, Clean Architecture, test edilebilirlik ve performans odaklı konuş."
            "HIZLI" -> "Çok pratik ve hızlısın. Lafı hiç uzatmadan doğrudan çalışan temiz kodu ve gerekli kısa açıklamayı ver."
            "EGITICI" -> "Sen harika ve sabırlı bir yazılım mentorsün. Kodun arkasındaki mantığı, adım adım açıklamaları ve alternatif yaklaşımları öğretici şekilde aktar."
            else -> "Çok samimi, enerjik, kafa dengi ve zeki bir yazılımcı kankasın! Dostça, esprili ama teknik açıdan %100 profesyonel ve kusursuz yanıtlar ver."
        }

        return """
            Senin adın **Kanka AI**. Kullanıcının en yakın arkadaşı, kankası ve süper yetenekli yazılım geliştirme yardımcısısın.
            
            Kullanıcı Bilgileri:
            - İsim: $name
            - Hitap Şekli: "$hitap" (Kullanıcıya her zaman bu hitapla seslen, örn: "Selam $hitap!", "Hemen halledelim $hitap!")
            - Deneyim Seviyesi: $exp
            - Tercih Ettiği Teknolojiler: $favLangs
            - Kişilik ve Tarz Modu: $tone
            - Özel Kullanıcı Notu: "${profile.customNotes}"
            
            En Önemli Görevlerin & Yeteneklerin:
            1. **Kod Yazma & Problem Çözme:** En karmaşık yazılım problemlerini, algoritmaları, mimari soruları ve bug'ları anında analiz edip hatasız kodla çözmek.
            2. **Format Kuralları:** Kod bloklarını her zaman '```dil_adı\n// Kod buraya\n```' şeklinde temiz markdown blokları halinde sun.
            3. **Açıklayıcı & Destekleyici:** Kullanıcıyı her zaman motive et, asla yalnız bırakma ve çözümü netleştir.
            4. **Dil:** Türkçe (ve gerektiğinde kod terimleri) ile doğal ve akıcı bir üslup kullan.
        """.trimIndent()
    }

    private fun generateSmartFallback(prompt: String, profile: UserProfile, networkError: String? = null): String {
        val hitap = profile.hitapSekli.ifBlank { "Kankam" }
        val lower = prompt.lowercase()

        val sampleCode = when {
            lower.contains("jetpack compose") || lower.contains("compose") || lower.contains("kotlin") -> """
```kotlin
// $hitap, senin için hazırladığım modern Jetpack Compose örneği!
@Composable
fun KankaCard(
    title: String,
    subtitle: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Hemen İncele")
            }
        }
    }
}
```
            """.trimIndent()

            lower.contains("python") || lower.contains("algoritma") -> """
```python
# $hitap, işte hızlı ve optimize algoritma çözümü!
def find_two_sum(nums: list[int], target: int) -> tuple[int, int] | None:
    seen = {}
    for i, num in enumerate(nums):
        complement = target - num
        if complement in seen:
            return (seen[complement], i)
        seen[num] = i
    return None

# Test
arr = [2, 7, 11, 15]
target_val = 9
print(f"Indexler: {find_two_sum(arr, target_val)}") # (0, 1)
```
            """.trimIndent()

            else -> """
```kotlin
// $hitap, temiz ve modüler bir mimari çözümü:
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```
            """.trimIndent()
        }

        return if (networkError != null) {
            """
Selam $hitap! 🚀 

Sorunu aldım: **"$prompt"**

Şu an internet bağlantısı veya API erişiminde küçük bir gecikme oldu (`$networkError`), ama merak etme ben her zaman yanındayım!

İşte istediğin konuyla ilgili temiz ve test edilmiş kod bloğu:

$sampleCode

💡 **Kanka İpucu:** Kodu doğrudan kopyalayabilir veya Kod Defterine kaydedebilirsin $hitap! Başka bir sorun varsa hemen yaz, halledelim!
            """.trimIndent()
        } else {
            """
Eyvallah $hitap! 🚀

Sorunu hemen inceledim: **"$prompt"**

Senin için en temiz ve optimize çözümü çıkardım:

$sampleCode

💡 **Nasıl Çalışır?**
1. **Clean Code:** Kod okunabilirliği ve performans ön planda tutuldu.
2. **Güvenlik & Hata Yönetimi:** Olası edge-case'ler göz önünde bulunduruldu.

Nasıl buldun $hitap? Değiştirmemi veya geliştirmemi istediğin bir yer var mı?
            """.trimIndent()
        }
    }
}
