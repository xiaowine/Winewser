package com.xiaowine.winebrowser.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.R
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.ArrowLeftIcon
import com.xiaowine.winebrowser.ui.ArrowRightIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.ui.component.TabCountBadge
import com.xiaowine.winebrowser.ui.component.Web
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableState
import com.xiaowine.winebrowser.utils.Utils.showToast
import top.yukonga.miuix.kmp.basic.FloatingToolbar
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.ToolbarPosition
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import java.util.Date

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

@SuppressLint("AutoboxingStateCreation", "SetJavaScriptEnabled")
@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
    var isInHtmlState by remember { mutableStateOf(true) }
    var titleState by remember { mutableStateOf("") }

    val historyList = rememberPreviewableState(
        realData = { AppConfig.title },
        previewData = "aaa",
        onSync = { AppConfig.title = it }
    )
    var webViewUrlState = remember { mutableStateOf<String>("") }
    val webViewState = remember { mutableStateOf<WebView?>(null) }

    // 监听返回键，与WebView绑定
    BackHandler(enabled = isInHtmlState) {
        val webView = webViewState.value
        if (webView?.canGoBack() == true) {
            // WebView可以后退，执行后退
            webView.goBack()
        } else {
            // WebView无法后退，退出应用或回到首页
            isInHtmlState = false
        }
    }

    val context = LocalContext.current
    Scaffold(
        topBar = {
            AnimatedVisibility(isInHtmlState) {
                HomeSearchBar(navController, titleState)
            }
        },
        content = { paddingValues ->

            AnimatedVisibility(!isInHtmlState) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HomeBigTitle()
                    Text(
                        historyList.value,
                        modifier = Modifier.clickable {
                            historyList.value = Date().toString()
                        })
                    HomeSearchBar(navController)
                    HomeShortcut()
                }
            }
            AnimatedVisibility(isInHtmlState) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Web(
                        onTitleChange = { titleState = it },
                        onPageStarted = {
                            webViewUrlState.value = it
                        },
                        webViewState = webViewState
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(isInHtmlState) {
                WebViewButtonBar(webViewState, webViewUrlState)
            }
        },
        floatingToolbar = {
            AnimatedVisibility(!isInHtmlState) {
                SearchToolbar()
            }
        },
        floatingToolbarPosition = ToolbarPosition.BottomEnd
    )

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


@Composable
fun SearchToolbar() {
    FloatingToolbar(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(8.dp),
        cornerRadius = 20.dp
    ) {
        Row {
            val iconsList = listOf(
                AddIcon,
                MenuIcon,
            )
            iconsList.forEach {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(28.dp),
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = it,
                        contentDescription = "Navigation action",
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun WebViewButtonBar(webViewState: MutableState<WebView?>, webViewUrlState: MutableState<String>) {
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

        val canGoBack = remember { mutableStateOf(false) }
        val canGoForward = remember { mutableStateOf(false) }
        val webView = webViewState.value

        LaunchedEffect(webViewUrlState.value, webView) {
            canGoBack.value = webView?.canGoBack() == true
            canGoForward.value = webView?.canGoForward() == true
        }


        for (i in 0 until 5) {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(32.dp)
                    .clickable(enabled = (i == 0 && canGoBack.value) || (i == 1 && canGoForward.value)) {
                        if (i == 0) {
                            webView?.goBack()
                        } else if (i == 1) {
                            webView?.goForward()
                        }
                        if (i <= 1) {
                            canGoBack.value = webView?.canGoBack() == true
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
                            0 -> if (canGoBack.value) {
                                MiuixTheme.colorScheme.onBackground
                            } else {
                                MiuixTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                            }

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


@Composable
fun HomeBigTitle() {
    val context: Context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = context.getString(R.string.app_name), fontSize = 50.sp, fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun HomeSearchBar(
    navController: NavController,
    text: String = "搜索或输入网址"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .padding(horizontal = 24.dp)
                .padding(bottom = 5.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(55.dp)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = 2.dp, color = MiuixTheme.colorScheme.onBackground, shape = SmoothRoundedCornerShape(15.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, indication = null
                ) {
                    navController.navigate("search")
                }, contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Search,
                    contentDescription = "Search",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = text,
                    color =
                        MiuixTheme.textStyles.main.color,
                    modifier =
                        Modifier.padding(start = 8.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun HomeShortcut() {
    val shortcuts = listOf("百度", "哔哩哔哩", "知乎", "GitHub")
    val current = LocalContext.current

    var scrollState = rememberScrollState()
    FlowLayout(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(scrollState),
        horizontalSpacing = 8.dp,
        verticalSpacing = 16.dp
    ) {
        shortcuts.forEach { item ->
            HomeShortcutItem(
                title = item, onClick = { current.showToast(item) })
        }
    }
}

@Composable
private fun HomeShortcutItem(
    title: String, onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(SmoothRoundedCornerShape(12.dp))
                .background(MiuixTheme.colorScheme.dividerLine)
                .clickable(onClick = onClick), contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MiuixIcons.Basic.Search,
                contentDescription = title,
                tint = MiuixTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = title, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp)
        )
    }
}
