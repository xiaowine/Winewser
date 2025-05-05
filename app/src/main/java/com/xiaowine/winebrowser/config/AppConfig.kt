package com.xiaowine.winebrowser.config

import com.drake.serialize.serialize.annotation.SerializeConfig
import com.drake.serialize.serialize.serial

@SerializeConfig(mmapID = "app_config")
object AppConfig {
    var isPreview: Boolean = true
    const val TITLE_DEFAULT = "Wine Browser"
    var title by serial(TITLE_DEFAULT, "title")
    val searchDefault = listOf("百度", "知乎", "B站", "https://www.limestart.cn/")
    var searchHistory: List<String> by serial(searchDefault, "search_history")
}