package com.example.data.repository

import com.example.data.local.SnippetDao
import com.example.data.model.SavedSnippet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SnippetRepository(private val snippetDao: SnippetDao) {
    val allSnippets: Flow<List<SavedSnippet>> = snippetDao.getAllSnippets()

    fun searchSnippets(query: String): Flow<List<SavedSnippet>> =
        snippetDao.searchSnippets(query)

    suspend fun saveSnippet(title: String, language: String, code: String, note: String = ""): Long = withContext(Dispatchers.IO) {
        val snippet = SavedSnippet(
            title = title.ifBlank { "Kod Parçası (${language.ifBlank { "Genel" }})" },
            language = language.ifBlank { "kotlin" },
            code = code,
            note = note,
            timestamp = System.currentTimeMillis()
        )
        snippetDao.insertSnippet(snippet)
    }

    suspend fun deleteSnippet(snippetId: Long) = withContext(Dispatchers.IO) {
        snippetDao.deleteSnippetById(snippetId)
    }
}
