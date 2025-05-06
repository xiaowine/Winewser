package com.xiaowine.winebrowser.ui.pages

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.config.AppConfig
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.browser.BrowserMenu
import com.xiaowine.winebrowser.ui.component.home.HomeHeadline
import com.xiaowine.winebrowser.ui.component.home.HomeSearchBar
import com.xiaowine.winebrowser.ui.component.home.HomeShortcut
import com.xiaowine.winebrowser.ui.component.home.HomeToolbar
import com.xiaowine.winebrowser.utils.ConfigUtils.rememberPreviewableState
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.ToolbarPosition
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun TestHomePage() {
    MiuixTheme {
        App()
    }
}


@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
    var isMenuState = rememberSaveable { mutableStateOf(false) }
    val testSate = rememberPreviewableState(
        realData = { AppConfig.title },
        previewData = AppConfig.TITLE_DEFAULT,
        onSync = { AppConfig.title = it }
    )

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeHeadline()
                Text(
                    testSate.value,
//                    modifier = Modifier.clickable {
////                        testSate.value = Date().toString()
//                    }
                )
                HomeSearchBar(navController)
                HomeShortcut(navController)
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
