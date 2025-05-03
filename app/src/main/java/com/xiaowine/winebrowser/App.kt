package com.xiaowine.winebrowser

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xiaowine.winebrowser.ui.pages.BrowserPage
import com.xiaowine.winebrowser.ui.pages.HomePage
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun App(
    startDestination: String = "home",
    navController: NavHostController = rememberNavController()
) {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    val isPreview = LocalInspectionMode.current
    AppConfig.isPreview = isPreview

    MiuixTheme(
        colors = colors
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            exitTransition = { fadeOut(targetAlpha = 1f) },
            enterTransition = { fadeIn(initialAlpha = 1f) },
        ) {
            pageDestinations(navController)
        }
    }
}


fun NavGraphBuilder.pageDestinations(
    navController: NavHostController
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
        val isSearch = backStackEntry.arguments?.getBoolean("isSearch")!!
        BrowserPage(
            navController = navController,
            urlToLoad = url,
            isSearch = isSearch
        )
    }
}
