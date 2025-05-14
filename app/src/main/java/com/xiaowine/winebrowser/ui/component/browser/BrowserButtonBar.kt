package com.xiaowine.winebrowser.ui.component.browser

import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.ArrowLeftIcon
import com.xiaowine.winebrowser.ui.ArrowRightIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BrowserButtonBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    webViewState: MutableState<WebView?>,
    webViewUrlState: MutableState<String>,
    isMenuState: MutableState<Boolean>,
    onCreateNewWebView: () -> Unit = {},
    tabCount: Int = 1
) {
    Row(
        modifier = modifier
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

        // 监听URL变化更新前进按钮状态
        LaunchedEffect(webViewUrlState.value, webView) {
            canGoForward.value = webView?.canGoForward() == true
        }

        // 渲染底部按钮
        for (i in 0 until 5) {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(32.dp)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        when (i) {
                            0 -> {
                                if (webView?.canGoBack() == true) {
                                    webView.goBack()
                                } else {
                                    navController.navigate("home") {
                                        popUpTo(0) { inclusive = false }
                                    }
                                }
                            }

                            1 -> {
                                webView?.goForward()
                            }

                            2 -> {
                                // 新建webview标签页
                                onCreateNewWebView()
                            }
                            4 -> {
                                isMenuState.value = !isMenuState.value
                            }
                        }
                        if (i <= 1) {
                            canGoForward.value = webView?.canGoForward() == true
                        }
                    },
            ) {
                if (i == 3) {
                    BrowserTabCountBadge(
                        count = tabCount,
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
