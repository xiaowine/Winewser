package com.xiaowine.winebrowser.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.xiaowine.winebrowser.ui.theme.AppColorsData


fun appLightColorScheme(): AppColorsData {
    return AppColorsData(
        homeSearchLineColor = Color("#333333".toColorInt()),
        homeShortBackgroundColor = Color("#e6e6e6".toColorInt()),
        searchTextColor = Color("#444746".toColorInt()),
        searchHistoryBackgroundColor = Color("#F7F7F8".toColorInt()),
        iconTintColor = Color("#444746".toColorInt()),
    )
}

fun appDarkColorScheme(): AppColorsData {
    return AppColorsData(
        homeSearchLineColor = Color("#999999".toColorInt()),
        homeShortBackgroundColor = Color("#2B2B2B".toColorInt()),
        searchTextColor = Color("#C4C7C5".toColorInt()),
        searchHistoryBackgroundColor = Color("#242424".toColorInt()),
        iconTintColor = Color("#C4C7C5".toColorInt()),
    )
}


val LocalAppColors = staticCompositionLocalOf { appLightColorScheme() }
