package com.xiaowine.winebrowser.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xiaowine.winebrowser.data.InitData
import com.xiaowine.winebrowser.data.entity.AppEntity
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.utils.Utils.getDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val shortcuts = MutableStateFlow<List<HomeShortcutEntity>>(emptyList())

    val isEditMode = mutableStateOf(false)
    val isLoading = mutableStateOf(true)

    val db = getDB(getApplication())

    init {
        viewModelScope.launch {
            isLoading.value = true
            initializeIfNeededAndLoad()
            isLoading.value = false
            db.homeShortcutData().getAll().collectLatest {
                shortcuts.value = it
            }
        }
    }

    private suspend fun initializeIfNeededAndLoad() {
        withContext(Dispatchers.IO) {
            val appEntity = db.appData().get()

            // 检查是否需要初始化
            if (appEntity == null || !appEntity.isInitialized) {
                db.runInTransaction {
                    // 添加默认快捷方式
                    db.homeShortcutData().insertAll(InitData.defaultShortcuts)
                }
                // 更新或创建应用配置
                if (appEntity == null) {
                    db.appData().insert(AppEntity(isInitialized = true))
                } else {
                    db.appData().update(appEntity.copy(isInitialized = true))
                }
            }

            // 加载最新的快捷方式列表
            shortcuts.value = db.homeShortcutData().getAllDirect()
        }
    }

    fun addShortcut(title: String, url: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newShortcut = HomeShortcutEntity(
                    title = title,
                    url = url
                )

                db.homeShortcutData().insert(newShortcut)
            }
        }
    }

    fun deleteShortcut(shortcut: HomeShortcutEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.homeShortcutData().delete(shortcut)
            }
        }
    }
}
