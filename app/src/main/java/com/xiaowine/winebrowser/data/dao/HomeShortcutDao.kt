package com.xiaowine.winebrowser.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeShortcutDao {
    @Query("SELECT * FROM home_shortcut_table ORDER BY id DESC")
    fun getAll(): Flow<List<HomeShortcutEntity>>
    
    // 添加直接获取数据的方法，不使用Flow
    @Query("SELECT * FROM home_shortcut_table ORDER BY id DESC")
    suspend fun getAllDirect(): List<HomeShortcutEntity>

    // 获取最后添加的一条记录
    @Query("SELECT * FROM home_shortcut_table ORDER BY id DESC LIMIT 1")
    suspend fun getLastAdded(): HomeShortcutEntity

    // 添加insertAll方法
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shortcuts: List<HomeShortcutEntity>)

    @Insert
    fun insert(shortcut: HomeShortcutEntity): Long

    @Delete
    suspend fun delete(shortcut: HomeShortcutEntity)

    @Update
    suspend fun update(entity: HomeShortcutEntity): Int
}
