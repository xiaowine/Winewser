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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
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
import com.xiaowine.winebrowser.data.WebViewTabData
import com.xiaowine.winebrowser.data.entity.SearchHistoryEntity
import com.xiaowine.winebrowser.utils.Utils.isColorSimilar
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

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

    // 标签页状态
    val tabs = remember { mutableStateListOf<WebViewTabData>() }
    val currentTabIndex = remember { mutableIntStateOf(0) }
    val searchText = remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    val isMenuState = rememberSaveable { mutableStateOf(false) }
    var isFieldFocused by remember { mutableStateOf(false) }
    var urlFromSearch by remember { mutableStateOf(false) }
    val initialUrlLoaded = remember { mutableStateOf(false) }

    // 当前标签页状态
    val currentTabProgressState = remember { mutableIntStateOf(0) }
    val currentTabIconState = remember { mutableStateOf<Bitmap?>(null) }

    // 主题颜色
    val themeColor = MiuixTheme.colorScheme.background

    // 初始化第一个标签页
    if (tabs.isEmpty() && webViewUrlState.value.isNotEmpty()) {
        tabs.add(WebViewTabData(url = webViewUrlState.value))
        initialUrlLoaded.value = true
    }

    // 当前标签页
    val currentTab = if (tabs.isNotEmpty() && currentTabIndex.intValue < tabs.size) {
        tabs[currentTabIndex.intValue]
    } else {
        WebViewTabData()
    }

    // 进度条显示逻辑
    val shouldShowProgress by remember(currentTabProgressState.intValue, isSearchState.value) {
        derivedStateOf {
            currentTabProgressState.intValue in 1..99 && !isSearchState.value
        }
    }

    // 功能：执行搜索或导航
    val performSearchOrNavigate = { query: String ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            // 确定最终加载的URL
            val url = if (Patterns.WEB_URL.matcher(trimmedQuery).matches() || trimmedQuery.contains("://")) {
                if (!trimmedQuery.contains("://")) "https://$trimmedQuery" else trimmedQuery
            } else {
                "https://cn.bing.com/search?q=${Uri.encode(trimmedQuery)}"
            }

            // 添加到搜索历史记录并管理历史记录
            searchHistoryViewModel.addSearchHistory(trimmedQuery)
            searchHistoryViewModel.clearOutdatedHistory(20)

            // 隐藏键盘，更新状态并加载URL
            focusManager.clearFocus()
            isSearchState.value = false
            webViewUrlState.value = url
            urlFromSearch = true
        }
    }

    // 功能：创建新标签页
    val createNewTab = {
        tabs.add(WebViewTabData())
        currentTabIndex.intValue = tabs.size - 1
        isSearchState.value = true // 新标签页自动进入搜索状态
        initialUrlLoaded.value = false // 重置初始URL加载状态
    }

    // 效果：当标签页切换时，更新WebView状态
    LaunchedEffect(currentTabIndex.intValue) {
        if (tabs.isNotEmpty() && currentTabIndex.intValue < tabs.size) {
            webViewState.value = currentTab.webView
            webViewUrlState.value = currentTab.url
        }
    }

    // 效果：监听WebViewUrlState的变化，更新当前标签的URL
    LaunchedEffect(webViewUrlState.value) {
        if (currentTabIndex.intValue < tabs.size) {
            tabs[currentTabIndex.intValue].url = webViewUrlState.value
        }
    }

    // 效果：监听WebView实例和URL变化，确保URL正确加载
    LaunchedEffect(webViewUrlState.value, webViewState.value) {
        if (isSearchState.value) return@LaunchedEffect

        val webView = webViewState.value
        val url = webViewUrlState.value

        if (webView != null && url.isNotEmpty()) {
            Log.d("Browser", "Loading URL: $url")
            webView.loadUrl(url)
            currentTab.title = "正在加载中..."
            currentTab.icon = null
            initialUrlLoaded.value = true
        }
    }

    // 效果：更新搜索框显示的标题
    LaunchedEffect(currentTab.title) {
        if (!isSearchState.value) {
            searchText.value = TextFieldValue(currentTab.title)
        }
    }

    // 效果：搜索状态变化时更新搜索框
    LaunchedEffect(isSearchState.value) {
        if (isSearchState.value) {
            focusRequester.requestFocus()
            searchText.value = TextFieldValue("")
        } else {
            searchText.value = TextFieldValue(currentTab.title)
        }
    }

    // 处理返回键逻辑
    BackHandler {
        handleBackPress(
            isFieldFocused = isFieldFocused,
            focusManager = focusManager,
            isSearchState = isSearchState,
            webViewState = webViewState,
            currentTab = currentTab,
            tabs = tabs,
            currentTabIndex = currentTabIndex,
            navController = navController
        )
    }

    // 主界面搭建
    Scaffold(
        topBar = {
            // 顶部搜索栏
            BrowserSearchField(
                modifier = Modifier
                    .background(
                        if (isSearchState.value || isSystemInDarkTheme())
                            MiuixTheme.colorScheme.background
                        else
                            Color(currentTab.themeColor)
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
                siteIconState = currentTabIconState,
                isLoading = { currentTab.progress in 1..99 }
            )

            // 进度条
            if (shouldShowProgress) {
                LinearProgressIndicator(
                    progress = currentTabProgressState.intValue / 100f
                )
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
                // 搜索状态下显示历史记录
                if (isSearchState.value) {
                    RenderSearchHistoryPanel(
                        currentTab = currentTab,
                        historyList = historyList,
                        searchText = searchText,
                        performSearchOrNavigate = performSearchOrNavigate
                    )
                }

                // 为每个标签页创建WebView
                RenderTabWebViews(
                    tabs = tabs,
                    currentTabIndex = currentTabIndex.intValue,
                    isSearchState = isSearchState.value,
                    webViewState = webViewState,
                    webViewUrlState = webViewUrlState,
                    currentTabIconState = currentTabIconState,
                    currentTabProgressState = currentTabProgressState,
                    searchText = searchText,
                    themeColor = themeColor
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
                                Color(currentTab.themeColor)
                        ),
                    navController = navController,
                    webViewState = webViewState,
                    webViewUrlState = webViewUrlState,
                    isMenuState = isMenuState,
                    onCreateNewWebView = createNewTab,
                    tabCount = tabs.size
                )
            }
        },
    )

    // 浏览器菜单
    BrowserMenu(isMenuState)

    // 调试模式下显示FPS监视器
    AnimatedVisibility(visible = BuildConfig.DEBUG) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .captionBarPadding()
                .padding(horizontal = 4.dp)
        )
    }
}

/**
 * 处理返回键逻辑
 */
private fun handleBackPress(
    isFieldFocused: Boolean,
    focusManager: FocusManager,
    isSearchState: MutableState<Boolean>,
    webViewState: MutableState<WebView?>,
    currentTab: WebViewTabData,
    tabs: MutableList<WebViewTabData>,
    currentTabIndex: MutableState<Int>,
    navController: NavController
) {
    // 处理焦点状态
    if (isFieldFocused) {
        focusManager.clearFocus()
        return
    }

    // 空标题页面返回主页
    if (currentTab.title.isEmpty()) {
        navController.navigate("home") {
            popUpTo(0) { inclusive = false }
        }
        return
    }

    // 处理搜索状态
    if (isSearchState.value) {
        isSearchState.value = false
        return
    }

    // 处理网页返回
    val webView = webViewState.value
    if (webView?.canGoBack() == true) {
        webView.goBack()
        return
    }

    // 处理多标签页
    if (tabs.size > 1 && currentTabIndex.value > 0) {
        // 移除当前标签页并切换到前一个
        tabs.removeAt(currentTabIndex.value)
        currentTabIndex.value -= 1
    } else {
        // 返回主页
        navController.navigate("home") {
            popUpTo(0) { inclusive = false }
        }
    }
}

/**
 * 渲染搜索历史面板
 */
@Composable
private fun RenderSearchHistoryPanel(
    currentTab: WebViewTabData,
    historyList: List<SearchHistoryEntity>,
    searchText: MutableState<TextFieldValue>,
    performSearchOrNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background)
            .padding(horizontal = 5.dp)
    ) {
        // 显示当前站点信息（如果有）
        if (currentTab.title.isNotEmpty()) {
            BrowserNowSiteInfo(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .padding(bottom = 10.dp),
                urlState = remember { mutableStateOf(currentTab.url) },
                titleState = remember { mutableStateOf(currentTab.title) },
                searchText = searchText,
            )
        }

        // 搜索历史面板
        BrowserSearchHistoryPanel(
            modifier = Modifier.fillMaxSize(),
            historyList = historyList,
            onSelected = performSearchOrNavigate
        )
    }
}

/**
 * 渲染所有标签页的WebView
 */
@Composable
private fun RenderTabWebViews(
    tabs: List<WebViewTabData>,
    currentTabIndex: Int,
    isSearchState: Boolean,
    webViewState: MutableState<WebView?>,
    webViewUrlState: MutableState<String>,
    currentTabIconState: MutableState<Bitmap?>,
    currentTabProgressState: MutableState<Int>,
    searchText: MutableState<TextFieldValue>,
    themeColor: Color
) {
    tabs.forEachIndexed { index, tab ->
        val isVisible = index == currentTabIndex && !isSearchState
        WebViewLayout(
            modifier = Modifier.fillMaxSize(),
            onTitleChange = { newTitle ->
                tab.title = newTitle
                if (index == currentTabIndex) {
                    searchText.value = TextFieldValue(newTitle)
                }
            },
            onIconChange = { newIcon ->
                tab.icon = newIcon
                if (index == currentTabIndex) {
                    currentTabIconState.value = newIcon
                }
            },
            onPageStarted = { loadedUrl ->
                if (loadedUrl.isNotEmpty() && index == currentTabIndex) {
                    webViewUrlState.value = loadedUrl
                    tab.url = loadedUrl
                }
            },
            onPageColorChange = { newColor ->
                val defaultColor = themeColor.value.toInt()
                if (newColor == -1) {
                    tab.themeColor = defaultColor
                    return@WebViewLayout
                }

                tab.themeColor = if (isColorSimilar(Color(newColor), themeColor)) {
                    defaultColor
                } else {
                    newColor
                }
            },
            onProgressChanged = { newProgress ->
                tab.progress = newProgress
                if (index == currentTabIndex) {
                    currentTabProgressState.value = newProgress
                }
            },
            onWebViewCreated = { webView ->
                tab.webView = webView
                if (index == currentTabIndex) {
                    webViewState.value = webView
                }
            },
            isVisible = isVisible
        )
    }
}
