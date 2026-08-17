package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * User Profile information for customizing Kanka AI's interactions.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Yazılımcı",
    val hitapSekli: String = "Kankam", // "Kankam", "Reis", "Hocam", "Dostum", "Üstat", "Kardeşim"
    val avatarId: String = "avatar_robot", // "avatar_robot", "avatar_dev", "avatar_ninja", "avatar_wizard", "avatar_cat", "avatar_rocket"
    val experienceLevel: String = "Mid-Level", // "Junior", "Mid-Level", "Senior", "Öğrenci", "Hobist"
    val favoriteLanguages: List<String> = listOf("Kotlin", "Jetpack Compose", "Python"),
    val personalityTone: String = "SAMIMI", // "SAMIMI", "KIDEMLI", "HIZLI", "EGITICI"
    val selectedModel: String = "gemini-3.5-flash", // "gemini-3.5-flash" (Kanka Flash) or "gemini-3.1-pro-preview" (Kanka Pro Kod Ustası)
    val customNotes: String = "Bana doğrudan çalışır temiz kod ve pratik açıklamalar ver.",
    val isSetupComplete: Boolean = true
)

/**
 * Chat Session for grouping messages.
 */
@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val title: String = "Yeni Sohbet",
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val tags: List<String> = emptyList()
)

/**
 * Individual chat message in a session.
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val messageId: Long = 0,
    val sessionId: Long,
    val isUser: Boolean,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val modelUsed: String? = null
)

/**
 * Saved Code Snippet in the Code Vault (Kod Defteri).
 */
@Entity(tableName = "saved_snippets")
data class SavedSnippet(
    @PrimaryKey(autoGenerate = true) val snippetId: Long = 0,
    val title: String,
    val language: String,
    val code: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val tags: List<String> = emptyList()
)

/**
 * Converters for Room database
 */
class RoomConverters {
    private val moshi = Moshi.Builder().build()
    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(listType)

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return adapter.toJson(value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(value) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
