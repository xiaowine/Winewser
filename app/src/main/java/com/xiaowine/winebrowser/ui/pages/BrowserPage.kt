package com.xiaowine.winebrowser.ui.pages

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.model.SearchHistoryViewModel
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.WebViewLayout
import com.xiaowine.winebrowser.ui.component.browser.BrowserButtonBar
import com.xiaowine.winebrowser.ui.component.browser.BrowserMenu
import com.xiaowine.winebrowser.ui.component.browser.BrowserNowSiteInfo
import com.xiaowine.winebrowser.ui.component.browser.BrowserSearchField
import com.xiaowine.winebrowser.ui.component.browser.BrowserSearchHistoryPanel
import com.xiaowine.winebrowser.utils.Utils.isColorSimilar
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

//@Composable
//@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
//@Preview(showSystemUi = true)
//@Preview(
//    showSystemUi = true,
//    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
//)
//fun TestBrowser() {
//    MiuixTheme {
//        App("browser?url=&isSearch=true")
//    }
//}

@Composable
fun BrowserPage(
    navController: NavController,
    isSearchState: MutableState<Boolean>,
    webViewUrlState: MutableState<String>,
    webViewState: MutableState<WebView?>
) {
    // 状态管理
    val focusManager = LocalFocusManager.current

    val searchHistoryViewModel = viewModel<SearchHistoryViewModel>()

    val historyList = searchHistoryViewModel.historyList.value


    var siteTitleState = remember { mutableStateOf("") }
    var siteIconState = remember { mutableStateOf<Bitmap?>(null) }
    var siteColorState = remember { mutableIntStateOf(android.graphics.Color.WHITE) }

    var searchText = remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    var isMenuState = rememberSaveable { mutableStateOf(false) }
    var progress = remember { mutableIntStateOf(0) }
    var isFieldFocused by remember { mutableStateOf(false) }
    var urlFromSearch by remember { mutableStateOf(false) }

    // 监听WebView实例和URL变化，确保URL正确加载
    LaunchedEffect(webViewUrlState.value) {
        if (isSearchState.value) return@LaunchedEffect
        val webView = webViewState.value
        val url = webViewUrlState.value

        if (webView != null && url.isNotEmpty()) {
            Log.d("Browser", "Loading URL: $url")
            webView.loadUrl(url)
            siteTitleState.value = "正在加载中..."
            siteIconState.value = null
        }
    }

    // 更新搜索框显示的标题
    LaunchedEffect(siteTitleState.value) {
        searchText.value = TextFieldValue(siteTitleState.value)
    }

    LaunchedEffect(isSearchState.value) {
        if (isSearchState.value) {
            focusRequester.requestFocus()
            searchText.value = TextFieldValue("")
        } else {
            searchText.value = TextFieldValue(siteTitleState.value)
        }
    }

    val color = MiuixTheme.colorScheme.background
    // 执行搜索或导航的 Lambda 函数
    val performSearchOrNavigate: (String) -> Unit = { query ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            // 确定最终加载的URL
            val url = if (Patterns.WEB_URL.matcher(trimmedQuery).matches() || trimmedQuery.contains("://")) {
                if (!trimmedQuery.contains("://")) "https://$trimmedQuery" else trimmedQuery
            } else {
                "https://cn.bing.com/search?q=${Uri.encode(trimmedQuery)}"
            }

            // 添加到搜索历史记录并刷新列表
            searchHistoryViewModel.addSearchHistory(trimmedQuery)
            // 清理旧记录，保留最新的20条
            searchHistoryViewModel.clearOutdatedHistory(20)

            // 隐藏键盘，更新状态并加载URL
            focusManager.clearFocus()
            isSearchState.value = false
            webViewUrlState.value = url
            urlFromSearch = true
        }
    }

    // 处理返回键逻辑
    BackHandler {
        if (isFieldFocused) {
            focusManager.clearFocus()
        }
        if (siteTitleState.value.isEmpty()) {
            navController.navigate("home") {
                popUpTo(0) { inclusive = false }
            }
            return@BackHandler
        }
        if (isSearchState.value) {
            isSearchState.value = false
        } else {
            val webView = webViewState.value
            if (webView?.canGoBack() == true) {
                webView.goBack()
            } else {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = false }
                }
            }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                // 搜索栏
                BrowserSearchField(
                    modifier = Modifier
                        .background(
                            if (isSearchState.value || isSystemInDarkTheme())
                                MiuixTheme.colorScheme.background
                            else
                                Color(siteColorState.intValue)
                        )
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 6.dp)
                        .onFocusChanged { focusState ->
                            isFieldFocused = focusState.isFocused
                            if (focusState.isFocused) {
                                isSearchState.value = true
                            }
                        },
                    searchText = searchText.value,
                    focusRequester = focusRequester,
                    onValueChange = {
                        searchText.value = it
                    },
                    onSearch = { performSearchOrNavigate(searchText.value.text) },
                    webViewState = webViewState,
                    isSearchState = isSearchState,
                    siteIconState = siteIconState,
                    isLoading = { progress.intValue != 100 }
                )

                // 加载进度条
                if (progress.intValue != 100 && !isSearchState.value) {
                    LinearProgressIndicator(progress = progress.intValue / 100f)
                }
            },
            content = { paddingValues ->
                // 添加一个全屏点击监听器作为最底层
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            if (isFieldFocused) {
                                focusManager.clearFocus()
                            }
                        }
                        .padding(paddingValues)
                ) {
                    // 搜索状态下显示历史记录，使用Box包裹以防止点击事件穿透
                    if (isSearchState.value) {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MiuixTheme.colorScheme.background)
                                .padding(horizontal = 5.dp)
                        ) {
                            if (siteTitleState.value.isNotEmpty()) {
                                BrowserNowSiteInfo(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .padding(top = 16.dp)
                                        .padding(bottom = 10.dp),
                                    urlState = webViewUrlState,
                                    titleState = siteTitleState,
                                    searchText = searchText,
                                )
                            }
                            BrowserSearchHistoryPanel(
                                modifier = Modifier.fillMaxSize(),
                                historyList = historyList,
                                onSelected = { performSearchOrNavigate(it) }
                            )
                        }
                    }
                    WebViewLayout(
                        modifier = Modifier.fillMaxSize(),
                        onTitleChange = { siteTitleState.value = it },
                        onIconChange = {
                            siteIconState.value = it
                        },
                        onPageStarted = { loadedUrl ->
                            println("onPageStarted")
                            if (loadedUrl.isNotEmpty() && loadedUrl != webViewUrlState.value && !urlFromSearch) {
                                webViewUrlState.value = loadedUrl
                            } else {
                                urlFromSearch = false
                            }
                        },
                        onPageColorChange = {
                            println("onPageColorChange")
                            val themeColor = color.value.toInt()
                            if (it == -1) siteColorState.intValue = themeColor
                            val similar = isColorSimilar(Color(it), color)
                            if (similar) {
                                siteColorState.intValue = themeColor
                            } else {
                                siteColorState.intValue = it
                            }
//                            siteColorState.intValue = it
                        },
                        onProgressChanged = { progress.intValue = it },
                        webViewState = webViewState,
                        isVisible = !isSearchState.value
                    )
                }
            },
            bottomBar = {
                // 非搜索状态下显示底部导航栏
                if (!isSearchState.value) {
                    BrowserButtonBar(
                        modifier = Modifier
                            .background(
                                if (isSystemInDarkTheme())
                                    MiuixTheme.colorScheme.background
                                else
                                    Color(siteColorState.intValue)
                            ),
                        navController = navController,
                        webViewState = webViewState,
                        webViewUrlState = webViewUrlState,
                        isMenuState = isMenuState
                    )
                }
            },
        )
        BrowserMenu(isMenuState)
    }

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

