package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SavedSnippet
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {
    @Query("SELECT * FROM saved_snippets ORDER BY timestamp DESC")
    fun getAllSnippets(): Flow<List<SavedSnippet>>

    @Query("SELECT * FROM saved_snippets WHERE snippetId = :snippetId LIMIT 1")
    suspend fun getSnippetById(snippetId: Long): SavedSnippet?

    @Query("SELECT * FROM saved_snippets WHERE title LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%' OR language LIKE '%' || :query || '%'")
    fun searchSnippets(query: String): Flow<List<SavedSnippet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SavedSnippet): Long

    @Update
    suspend fun updateSnippet(snippet: SavedSnippet)

    @Query("DELETE FROM saved_snippets WHERE snippetId = :snippetId")
    suspend fun deleteSnippetById(snippetId: Long)
}
