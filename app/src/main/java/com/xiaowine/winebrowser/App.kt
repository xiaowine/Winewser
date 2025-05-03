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
import com.xiaowine.winebrowser.ui.pages.Browser
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
        Browser(
            navController = navController,
            urlToLoad = url,
            isSearch = isSearch
        )
    }

//// 更新现有的web路由，重定向到新的combined页面
//    composable(
//        route = "web?url={url}",
//        arguments = listOf(navArgument("url") { type = NavType.StringType })
//    ) { backStackEntry ->
//        val url = backStackEntry.arguments?.getString("url")
//        LaunchedEffect(Unit) {
//            navController.navigate("browser?url=$url") {
//                popUpTo("web?url=$url") { inclusive = true }
//            }
//        }
//    }

//    composable(
//        route = "web?url={url}", // 路由路径，包含可选的 url 参数
//        arguments = listOf(navArgument("url") { // 定义 url 参数
//            type = NavType.StringType // 参数类型为字符串
//            nullable = true // 参数可为空
//        })
//    ) { backStackEntry ->
//        // 获取传递过来的 url 参数
//        val url = backStackEntry.arguments?.getString("url")
//        // 加载 HomePage Composable
//        WebPage(navController, url)
//    }
//    composable(
//        route = "search",
//        enterTransition = {
//            slideIntoContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Up,
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioLowBouncy,
//                    stiffness = Spring.StiffnessLow
//                )
//            )
//        },
//        exitTransition = {
//            slideOutOfContainer(
//                towards = AnimatedContentTransitionScope.SlideDirection.Down,
//                animationSpec = spring(
//                    dampingRatio = Spring.DampingRatioNoBouncy,
//                    stiffness = Spring.StiffnessMediumLow
//                )
//            )
//        },
//    ) {
//        SearchPage(navController)
//    }
}
