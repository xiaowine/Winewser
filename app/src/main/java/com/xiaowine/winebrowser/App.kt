package com.xiaowine.winebrowser

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xiaowine.winebrowser.ui.pages.HomePage
import com.xiaowine.winebrowser.ui.pages.SearchPage
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun App() {
    val navController = rememberNavController()

    val currentRoute = remember {
        mutableStateOf(navController.currentDestination?.route ?: "")
    }
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }
    MiuixTheme(
        colors = colors
    ) {
        NavHost(
            navController = navController,
            startDestination = "home",
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
        route = "search",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(
                animationSpec = spring(),
                initialAlpha = 0.5f
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeOut(
                animationSpec = spring(),
                targetAlpha = 1f
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(
                animationSpec = spring(),
                initialAlpha = 0.5f
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeOut(
                animationSpec = spring(),
                targetAlpha = 1f
            )
        }
    ) { SearchPage() }
}
