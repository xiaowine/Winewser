package com.xiaowine.winebrowser

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import com.xiaowine.winebrowser.ui.pages.HomePage
import com.xiaowine.winebrowser.ui.pages.SearchPage
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

/**
 * 应用的主 Composable 函数
 * 设置主题、导航和全局配置
 *
 * @param startDestination 导航的起始页面路由
 * @param navController 导航控制器实例
 */
@Composable
fun App(
    startDestination: String = "home", // 默认起始页为 "home"
    navController: NavHostController = rememberNavController() // 创建或获取导航控制器
) {
    // 根据系统主题选择亮色或暗色配色方案
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    // 判断当前是否处于预览模式
    val isPreview = LocalInspectionMode.current
    // 将预览状态存入 AppConfig
    AppConfig.isPreview = isPreview

    MiuixTheme(
        colors = colors
    ) {
        // 设置导航宿主
        NavHost(
            navController = navController,
            startDestination = startDestination,
            // 设置页面切换时的退出动画（淡出）
            exitTransition = { fadeOut(targetAlpha = 1f) },
            // 设置页面切换时的进入动画（淡入）
            enterTransition = { fadeIn(initialAlpha = 1f) },
        ) {
            // 定义应用的页面路由和导航逻辑
            pageDestinations(navController)
        }
    }
}

/**
 * 定义应用的页面导航图
 *
 * @param navController 导航控制器实例
 */
fun NavGraphBuilder.pageDestinations(
    navController: NavHostController
) {
    // 定义 "home" 页面的路由
    composable(
        route = "home?url={url}", // 路由路径，包含可选的 url 参数
        arguments = listOf(navArgument("url") { // 定义 url 参数
            type = NavType.StringType // 参数类型为字符串
            nullable = true // 参数可为空
        })
    ) { backStackEntry ->
        // 获取传递过来的 url 参数
        val url = backStackEntry.arguments?.getString("url")
        // 加载 HomePage Composable
        HomePage(navController, url)
    }

    // 定义 "search" 页面的路由
    composable(
        route = "search",
        // 自定义进入动画（从下方滑入并淡入）
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy, // 无弹性阻尼
                    stiffness = Spring.StiffnessMediumLow // 中低刚度
                ),
                initialOffset = { it / 3 } // 初始偏移量
            ) + fadeIn()
        },
        // 自定义退出动画（向下方滑出并淡出）
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy, // 无弹性阻尼
                    stiffness = Spring.StiffnessMediumLow // 中低刚度
                ),
                targetOffset = { it / 3 } // 目标偏移量
            ) + fadeOut()
        },
    ) {
        // 加载 SearchPage Composable
        SearchPage(navController)
    }
}
