package com.xiaowine.winebrowser

import com.drake.serialize.serialize.annotation.SerializeConfig
import com.drake.serialize.serialize.serial

@SerializeConfig(mmapID = "app_config")
object AppConfig {
    var isPreview: Boolean = true
    var title by serial("Wine Browser", "title")
    val searchDefault = listOf("百度", "知乎", "B站", "https://www.limestart.cn/")
    var searchHistory: List<String> by serial(searchDefault, "search_history")
}
