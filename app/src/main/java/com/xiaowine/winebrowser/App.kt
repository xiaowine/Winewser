package com.xiaowine.winebrowser

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xiaowine.winebrowser.utils.Utils
import com.xiaowine.winebrowser.ui.appDarkColorScheme
import com.xiaowine.winebrowser.ui.appLightColorScheme
import com.xiaowine.winebrowser.ui.pages.BrowserPage
import com.xiaowine.winebrowser.ui.pages.HomePage
import com.xiaowine.winebrowser.ui.theme.AppTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import android.webkit.WebView

@Composable
fun App(
    startDestination: String = "home",
    navController: NavHostController = rememberNavController()
) {
    val theme = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }
    val appTheme = if (isSystemInDarkTheme()) {
        appDarkColorScheme()
    } else {
        appLightColorScheme()
    }

    val isPreview = LocalInspectionMode.current
    Utils.isPreview = isPreview

    // 用 rememberSaveable 保持 isSearchState 状态
    val isSearchState = rememberSaveable { mutableStateOf(false) }
    // 提升 webViewUrlState 和 webViewState
    val webViewUrlState = rememberSaveable { mutableStateOf("") }
    val webViewState = remember { mutableStateOf<WebView?>(null) }


    AppTheme(
        colors = theme,
        appColors = appTheme
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            exitTransition = { fadeOut(targetAlpha = 1f) },
            enterTransition = { fadeIn(initialAlpha = 1f) },
        ) {
            pageDestinations(navController, isSearchState, webViewUrlState, webViewState)
        }
    }
}

// 修改 pageDestinations，传递 isSearchState
fun NavGraphBuilder.pageDestinations(
    navController: NavHostController,
    isSearchState: androidx.compose.runtime.MutableState<Boolean>,
    webViewUrlState: androidx.compose.runtime.MutableState<String>,
    webViewState: androidx.compose.runtime.MutableState<WebView?>
) {
    composable("home") { HomePage(navController) }
    composable(
        route = "browser?url={url}&isSearch={isSearch}",
        arguments = listOf(
            navArgument("url") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("isSearch") {
                type = NavType.BoolType
                defaultValue = false
            }
        )
    ) { backStackEntry ->
        val url = backStackEntry.arguments?.getString("url")
        // 只在首次进入时用参数初始化
        if (backStackEntry.savedStateHandle.get<Boolean?>("isSearchState") == null) {
            val isSearch = backStackEntry.arguments?.getBoolean("isSearch") == true
            isSearchState.value = isSearch
            backStackEntry.savedStateHandle["isSearchState"] = isSearch
            // 初始化 webViewUrlState
            if (!url.isNullOrEmpty()) {
                webViewUrlState.value = url
            }
        }
        BrowserPage(
            navController = navController,
            isSearchState = isSearchState,
            webViewUrlState = webViewUrlState,
            webViewState = webViewState
        )
    }
}
