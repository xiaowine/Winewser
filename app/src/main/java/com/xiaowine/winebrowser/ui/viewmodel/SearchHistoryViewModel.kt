package com.xiaowine.winebrowser.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xiaowine.winebrowser.data.entity.SearchHistoryEntity
import com.xiaowine.winebrowser.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Utils.getDB(application)


    val historyList = mutableStateOf<List<SearchHistoryEntity>>(emptyList())

    init {
        // 使用 Flow 订阅数据变化
        viewModelScope.launch {
            db.historyData().getAll().collectLatest { history ->
                historyList.value = history
            }
        }
    }

    // 不再需要手动刷新方法
    // 删除 refreshHistoryList() 方法
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
            // Flow 会自动通知数据变化，不需要手动刷新
        }
    }

    fun clearOutdatedHistory(keepCount: Int = 20) {
        viewModelScope.launch(Dispatchers.IO) {
            db.historyData().deleteOldEntries(keepCount)
            // Flow 会自动通知数据变化，不需要手动刷新
        }
    }
}