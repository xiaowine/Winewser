package com.xiaowine.winebrowser.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.browser.BrowserMenu
import com.xiaowine.winebrowser.ui.component.home.HomeHeadline
import com.xiaowine.winebrowser.ui.component.home.HomeSearchBar
import com.xiaowine.winebrowser.ui.component.home.HomeShortcut
import com.xiaowine.winebrowser.ui.component.home.HomeToolbar
import com.xiaowine.winebrowser.ui.viewmodel.HomeViewModel
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.ToolbarPosition

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
    val viewModel: HomeViewModel = viewModel()
    var isMenuState = rememberSaveable { mutableStateOf(false) }
    var testSate by remember { mutableStateOf("Wine Browser") }
    val context = LocalContext.current

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        if (viewModel.isEditMode.value) viewModel.isEditMode.value = false
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeHeadline()
                Text(
                    testSate,
                )
                HomeSearchBar(navController)
                HomeShortcut(navController, viewModel)
            }
        },
        floatingToolbar = {
            HomeToolbar(isMenuState)
        },
        floatingToolbarPosition = ToolbarPosition.BottomEnd
    )
    BrowserMenu(isMenuState)
    AnimatedVisibility(
        visible = BuildConfig.DEBUG
    ) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .captionBarPadding()
                .padding(horizontal = 4.dp)
        )
    }
}
