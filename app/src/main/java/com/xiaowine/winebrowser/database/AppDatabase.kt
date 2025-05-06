package com.xiaowine.winebrowser.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xiaowine.winebrowser.data.SearchHistoryDao
import com.xiaowine.winebrowser.entity.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyData(): SearchHistoryDao
}