package com.xiaowine.winebrowser.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.xiaowine.winebrowser.entity.SearchHistoryEntity

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history ORDER BY id DESC")
    suspend fun getAll(): List<SearchHistoryEntity>

    @Insert
    suspend fun insert(history: SearchHistoryEntity)

    @Delete
    suspend fun delete(history: SearchHistoryEntity)

    @Query("SELECT * FROM search_history WHERE content = :content LIMIT 1")
    suspend fun findByContent(content: String): SearchHistoryEntity?

    @Query("DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY id DESC LIMIT :keepCount)")
    suspend fun deleteOldEntries(keepCount: Int)

//    @Query("SELECT * FROM search_history WHERE content LIKE '%' || :content || '%' ORDER BY id DESC")
//    fun findByPartialContent(content: String): LiveData<List<SearchHistoryEntity>>
}
