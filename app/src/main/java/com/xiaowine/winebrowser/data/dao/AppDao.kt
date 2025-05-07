package com.xiaowine.winebrowser.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xiaowine.winebrowser.data.entity.AppEntity

@Dao
interface AppDao {
    @Query("SELECT * FROM app_table ORDER BY id DESC LIMIT 1")
    suspend fun get(): AppEntity?

    @Insert
    suspend fun insert(entity: AppEntity)

    @Update
    suspend fun update(entity: AppEntity): Int

    @Query("DELETE FROM app_table")
    suspend fun clear()
}
