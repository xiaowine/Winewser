package com.xiaowine.winebrowser.model

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.xiaowine.winebrowser.database.AppDatabase
import com.xiaowine.winebrowser.entity.SearchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application, AppDatabase::class.java, "database-name"
    ).build()

    // 使用 MutableState 替代 LiveData
    val historyList = mutableStateOf<List<SearchHistoryEntity>>(emptyList())

    init {
        // 初始化时加载历史记录
        refreshHistoryList()
    }

    // 刷新历史记录列表
    fun refreshHistoryList() {
        viewModelScope.launch(Dispatchers.IO) {
            val history = db.historyData().getAll()
            historyList.value = history
        }
    }

    fun addSearchHistory(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // 检查是否已存在相同内容
            val existingEntry = db.historyData().findByContent(query)
            if (existingEntry != null) {
                // 如果已存在，先删除旧记录
                db.historyData().delete(existingEntry)
            }
            // 插入新记录
            db.historyData().insert(SearchHistoryEntity(content = query))
            
            // 刷新历史记录列表
            refreshHistoryList()
        }
    }

    fun clearOutdatedHistory(keepCount: Int = 20) {
        viewModelScope.launch(Dispatchers.IO) {
            db.historyData().deleteOldEntries(keepCount)
            
            // 刷新历史记录列表
            refreshHistoryList()
        }
    }
}
