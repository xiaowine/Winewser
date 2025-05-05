package com.xiaowine.winebrowser.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.xiaowine.winebrowser.ui.LocalAppColors
import top.yukonga.miuix.kmp.theme.Colors
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.TextStyles

@Composable
fun AppTheme(
    colors: Colors = MiuixTheme.colorScheme,
    textStyles: TextStyles = MiuixTheme.textStyles,
    appColors: AppColorsData = AppTheme.colorScheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppColors provides appColors,
    ) {
        MiuixTheme(
            colors = colors,
            textStyles = textStyles
        ) {
            content()
        }
    }
}

object AppTheme {
    val colorScheme: AppColorsData
        @Composable
        get() = LocalAppColors.current
}