package com.xiaowine.winebrowser.ui.pages

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.ArrowLeftIcon
import com.xiaowine.winebrowser.ui.ArrowRightIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.HomeSearchBar
import com.xiaowine.winebrowser.ui.component.TabCountBadge
import com.xiaowine.winebrowser.ui.component.Web
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun WebPage(
    navController: NavController,
    urlToLoad: String? = null
) {

    var titleState by remember { mutableStateOf("") }

    var webViewUrlState = remember { mutableStateOf<String>("") }
    val webViewState = remember { mutableStateOf<WebView?>(null) }

// 确定进度的线性进度条
    var progress by remember { mutableIntStateOf(0) }

    // 监听返回键，与WebView绑定
    BackHandler {
        val webView = webViewState.value
        if (webView?.canGoBack() == true) {
            // WebView可以后退，执行后退
            webView.goBack()
        } else {
            navController.navigate("home") {
                popUpTo(0) { inclusive = false }
            }
        }
    }

    Scaffold(
        topBar = {
            HomeSearchBar(navController, titleState)
            if (progress != 100) {
                LinearProgressIndicator(progress = progress / 100f)
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Web(
                    initUrl = urlToLoad!!,
                    onTitleChange = { titleState = it },
                    onPageStarted = {
                        webViewUrlState.value = it
                    },
                    onProgressChanged = {
                        progress = it
                    },
                    webViewState = webViewState
                )
            }
        },
        bottomBar = {
            WebViewButtonBar(
                navController,
                webViewState,
                webViewUrlState
            )
        },
    )
}

@Composable
fun WebViewButtonBar(
    navController1: NavController,
    webViewState: MutableState<WebView?>,
    webViewUrlState: MutableState<String>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val iconsList = listOf(
            ArrowLeftIcon,
            ArrowRightIcon,
            AddIcon,
            null,
            MenuIcon,
        )

        val canGoForward = remember { mutableStateOf(false) }
        val webView = webViewState.value

        LaunchedEffect(webViewUrlState.value, webView) {
            canGoForward.value = webView?.canGoForward() == true
        }


        for (i in 0 until 5) {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(32.dp)
                    .clickable(enabled = (i == 0) || (i == 1 && canGoForward.value)) {
                        if (i == 0) {
                            if (webView?.canGoBack() == true) {
                                webView.goBack()
                            } else {
                                navController1.navigate("home") {
                                    popUpTo(0) { inclusive = false }
                                }
                            }
                        } else if (i == 1) {
                            webView?.goForward()
                        }
                        if (i <= 1) {
                            canGoForward.value = webView?.canGoForward() == true
                        }
                    },
            ) {
                if (i == 3) {
                    TabCountBadge(
                        count = 1,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                } else {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = iconsList[i]!!,
                        contentDescription = "Navigation action",
                        tint = when (i) {
                            1 -> if (canGoForward.value) {
                                MiuixTheme.colorScheme.onBackground
                            } else {
                                MiuixTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            }

                            else -> MiuixTheme.colorScheme.onBackground
                        }
                    )
                }
            }
        }
    }
}

