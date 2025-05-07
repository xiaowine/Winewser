package com.xiaowine.winebrowser.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.xiaowine.winebrowser.data.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history_table ORDER BY id DESC")
    fun getAll(): Flow<List<SearchHistoryEntity>>

    @Insert
    suspend fun insert(history: SearchHistoryEntity)

    @Delete
    suspend fun delete(history: SearchHistoryEntity)

    @Query("SELECT * FROM search_history_table WHERE content = :content LIMIT 1")
    suspend fun findByContent(content: String): SearchHistoryEntity?

    @Query("DELETE FROM search_history_table WHERE id NOT IN (SELECT id FROM search_history_table ORDER BY id DESC LIMIT :keepCount)")
    suspend fun deleteOldEntries(keepCount: Int)
}
