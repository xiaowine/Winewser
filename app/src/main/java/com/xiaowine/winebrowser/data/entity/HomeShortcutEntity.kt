package com.xiaowine.winebrowser.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_shortcut_table")
data class HomeShortcutEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Int = 0,
    @ColumnInfo("title")
    val title: String,
    @ColumnInfo("url")
    val url: String,
    @ColumnInfo("base64Icon")
    val base64Icon: String? = null
) {
    constructor(title: String, url: String, base64Icon: String?) : this(0, title, url, base64Icon)
}
