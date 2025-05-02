package com.xiaowine.winebrowser

import com.drake.serialize.serialize.annotation.SerializeConfig
import com.drake.serialize.serialize.serial

@SerializeConfig(mmapID = "app_config")
object AppConfig {
    var isPreview: Boolean = true
    var title by serial("Wine Browser", "title")
    var searchHistory: List<String> by serial(arrayListOf(), "search_history")
}
