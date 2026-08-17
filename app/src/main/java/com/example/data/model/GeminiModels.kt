package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiGenerateRequest(
    @field:Json(name = "contents") val contents: List<GeminiContent>,
    @field:Json(name = "systemInstruction") val systemInstruction: GeminiSystemInstruction? = null,
    @field:Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiSystemInstruction(
    @field:Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @field:Json(name = "role") val role: String? = null, // "user" or "model"
    @field:Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @field:Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @field:Json(name = "temperature") val temperature: Float = 0.7f,
    @field:Json(name = "topP") val topP: Float = 0.95f,
    @field:Json(name = "topK") val topK: Int = 40
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
    @field:Json(name = "candidates") val candidates: List<GeminiCandidate>? = null,
    @field:Json(name = "error") val error: GeminiErrorResponse? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @field:Json(name = "content") val content: GeminiContent? = null,
    @field:Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiErrorResponse(
    @field:Json(name = "code") val code: Int? = null,
    @field:Json(name = "message") val message: String? = null,
    @field:Json(name = "status") val status: String? = null
)
