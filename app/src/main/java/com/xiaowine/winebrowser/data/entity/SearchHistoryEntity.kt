package com.xiaowine.winebrowser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity

@Entity(tableName = "search_history_table")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("content")
    val content: String
) {
    constructor(content: String) : this(0, content)
}
