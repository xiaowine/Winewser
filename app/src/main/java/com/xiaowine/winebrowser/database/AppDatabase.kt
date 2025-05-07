package com.xiaowine.winebrowser.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xiaowine.winebrowser.data.dao.AppDao
import com.xiaowine.winebrowser.data.dao.HomeShortcutDao
import com.xiaowine.winebrowser.data.dao.SearchHistoryDao
import com.xiaowine.winebrowser.data.entity.AppEntity
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.data.entity.SearchHistoryEntity

@Database(entities = [SearchHistoryEntity::class, HomeShortcutEntity::class, AppEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyData(): SearchHistoryDao
    abstract fun homeShortcutData(): HomeShortcutDao
    abstract fun appData(): AppDao
}