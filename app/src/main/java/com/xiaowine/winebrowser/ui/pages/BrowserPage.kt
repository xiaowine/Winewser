package com.xiaowine.winebrowser.ui.pages

import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xiaowine.winebrowser.data.WebViewTabData
import com.xiaowine.winebrowser.data.entity.SearchHistoryEntity
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.WebViewLayout
import com.xiaowine.winebrowser.ui.component.browser.BrowserButtonBar
import com.xiaowine.winebrowser.ui.component.browser.BrowserMenu
import com.xiaowine.winebrowser.ui.component.browser.BrowserNowSiteInfo
import com.xiaowine.winebrowser.ui.component.browser.BrowserSearchField
import com.xiaowine.winebrowser.ui.component.browser.BrowserSearchHistoryPanel
import com.xiaowine.winebrowser.ui.component.browser.BrowserTab
import com.xiaowine.winebrowser.ui.viewmodel.SearchHistoryViewModel
import com.xiaowine.winebrowser.utils.Utils.isColorSimilar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 浏览器页面组件
 */
@Composable
fun BrowserPage(
    navController: NavController,
    isSearchState: MutableState<Boolean>,
    webViewUrlState: MutableState<String>,
    webViewState: MutableState<WebView?>
) {
    // 工具与依赖
    val focusManager = LocalFocusManager.current
    val searchHistoryViewModel = viewModel<SearchHistoryViewModel>()
    val historyList = searchHistoryViewModel.historyList.value
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    // 界面状态 - 可合并为一个对象的状态放在一起
    val uiState = remember {
        object {
            val isMenuOpenState = mutableStateOf(false)
            val isTabMenuOpenState = mutableStateOf(false)
            var isFieldFocused = mutableStateOf(false)
            val searchText = mutableStateOf(TextFieldValue(""))
            val currentTabProgress = mutableIntStateOf(0)
            val currentTabIcon = mutableStateOf<Bitmap?>(null)
        }
    }

    // 加载状态 - 相关的加载状态放在一起
    val loadState = remember {
        object {
            var isUrlFromSearch = mutableStateOf(false)
            val isInitialUrlLoaded = mutableStateOf(false)
            val isWebViewReady = mutableStateOf(false)
            val isFirstPageLoading = mutableStateOf(false)
            val isNewTabCreated = mutableStateOf(false)
            val pendingSearchQuery = mutableStateOf("")
            val initialUrl = mutableStateOf(webViewUrlState.value)
        }
    }

    // 标签页管理
    val tabs = remember { mutableStateListOf<WebViewTabData>() }
    val currentTabIndex = remember { mutableIntStateOf(0) }

    // 当前标签页
    val currentTab = if (tabs.isNotEmpty() && currentTabIndex.intValue < tabs.size) {
        tabs[currentTabIndex.intValue]
    } else {
        WebViewTabData()
    }

    // 主题颜色
    val themeColor = MiuixTheme.colorScheme.background

    // 进度条显示逻辑
    val shouldShowProgress by remember(uiState.currentTabProgress.intValue, isSearchState.value) {
        derivedStateOf {
            uiState.currentTabProgress.intValue in 1..99 && !isSearchState.value
        }
    }


    // Tab 操作函数
    val tabOperations = object {
        // 执行搜索或导航
        val performSearchOrNavigate: (String) -> Unit = { query ->
            val trimmedQuery = query.trim()
            if (trimmedQuery.isNotEmpty()) {
                // 构建URL
                val url = buildSearchOrNavigateUrl(trimmedQuery)

                // 添加到搜索历史记录
                searchHistoryViewModel.addSearchHistory(trimmedQuery)
                searchHistoryViewModel.clearOutdatedHistory(20)

                if (isSearchState.value && currentTab.webView != null) {
                    // 立即加载URL
                    loadUrlInCurrentTab(
                        url = url,
                        focusManager = focusManager,
                        isSearchState = isSearchState,
                        webViewUrlState = webViewUrlState,
                        urlFromSearch = loadState.isUrlFromSearch,
                        currentTab = currentTab
                    )
                } else {
                    // 保存查询以后续加载
                    loadState.pendingSearchQuery.value = url
                    focusManager.clearFocus()
                    isSearchState.value = false
                }
            }
        }

        // 创建新标签页
        val createNewTab: () -> Unit = {
            val newTab = WebViewTabData()
            tabs.add(newTab)
            currentTabIndex.intValue = tabs.size - 1

            // 重置状态
            isSearchState.value = true
            loadState.isInitialUrlLoaded.value = false
            loadState.isWebViewReady.value = false
            loadState.isNewTabCreated.value = true

            // 重置当前标签状态
            uiState.currentTabProgress.intValue = 0
            uiState.currentTabIcon.value = null
        }

        // 关闭标签页
        val closeTab: (WebViewTabData) -> Unit = { tab ->
            val index = tabs.indexOf(tab)
            if (index != -1) {
                if (tabs.size > 1) {
                    // 关闭标签页并调整当前索引
                    tabs.removeAt(index)
                    if (index <= currentTabIndex.intValue) {
                        currentTabIndex.intValue = (currentTabIndex.intValue - 1).coerceAtLeast(0)
                    }
                    // 更新当前标签页状态
                    if (tabs.isNotEmpty()) {
                        webViewState.value = tabs[currentTabIndex.intValue].webView
                        webViewUrlState.value = tabs[currentTabIndex.intValue].url
                    }
                } else {
                    // 如果是最后一个标签页，返回主页
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = false }
                    }
                }
            }
        }

        // 切换标签页
        val switchToTab: (WebViewTabData) -> Unit = { tab ->
            val index = tabs.indexOf(tab)
            if (index != -1) {
                // 获取当前标签页的截图（如果没有的话）
                if (tab.thumbnail == null) {
                    captureWebViewSnapshot(tab, coroutineScope)
                }

                // 设置当前标签页
                currentTabIndex.intValue = index

                // 更新WebView状态，切换WebView显示
                webViewState.value = tab.webView
                webViewUrlState.value = tab.url
                isSearchState.value = false

                // 更新UI状态
                uiState.currentTabProgress.intValue = tab.progress
                uiState.currentTabIcon.value = tab.icon
            }
        }
    }

    // 初始化第一个标签页
    LaunchedEffect(Unit) {
        if (tabs.isEmpty()) {
            tabs.add(WebViewTabData(url = loadState.initialUrl.value))
        }
    }

    // 当WebView准备好且初始URL存在时，触发加载
    LaunchedEffect(loadState.isWebViewReady.value, tabs.isNotEmpty()) {
        if (loadState.isWebViewReady.value && tabs.isNotEmpty() && !loadState.isFirstPageLoading.value) {
            val currentTab = tabs[currentTabIndex.intValue]
            val webView = currentTab.webView

            if (webView != null && loadState.initialUrl.value.isNotEmpty()) {
                Log.d("Browser", "首次加载URL: ${loadState.initialUrl.value}")
                loadState.isFirstPageLoading.value = true

                // 确保WebView已完全初始化
                delay(100)

                // 加载初始URL
                currentTab.url = loadState.initialUrl.value
                webView.loadUrl(loadState.initialUrl.value)
                loadState.isInitialUrlLoaded.value = true
            }
        }
    }

    // 处理新创建的标签页
    LaunchedEffect(loadState.isWebViewReady.value, loadState.isNewTabCreated.value) {
        if (loadState.isWebViewReady.value && loadState.isNewTabCreated.value) {
            // 重置标记
            loadState.isNewTabCreated.value = false

            // 检查是否有待处理的搜索查询
            if (loadState.pendingSearchQuery.value.isNotEmpty()) {
                Log.d("Browser", "加载新标签页待处理查询: ${loadState.pendingSearchQuery.value}")

                val currentTab = tabs[currentTabIndex.intValue]
                val webView = currentTab.webView

                if (webView != null) {
                    // 加载URL
                    currentTab.url = loadState.pendingSearchQuery.value
                    webViewUrlState.value = loadState.pendingSearchQuery.value
                    webView.loadUrl(loadState.pendingSearchQuery.value)

                    // 清空待处理查询
                    loadState.pendingSearchQuery.value = ""

                    // 关闭搜索状态
                    isSearchState.value = false
                }
            } else {
                // 如果没有查询，保持搜索状态以便用户输入
                isSearchState.value = true
                focusRequester.requestFocus()
            }
        }
    }

    // 效果：当标签页切换时，更新WebView状态
    LaunchedEffect(currentTabIndex.intValue) {
        if (tabs.isNotEmpty() && currentTabIndex.intValue < tabs.size) {
            val currentTab = tabs[currentTabIndex.intValue]

            // 直接更改WebView状态，不触发页面重载
            webViewState.value = currentTab.webView

            // 更新状态但不触发加载，如果URL相同
            if (webViewUrlState.value != currentTab.url) {
                webViewUrlState.value = currentTab.url
            }

            // 更新界面状态
            uiState.currentTabProgress.intValue = currentTab.progress
            uiState.currentTabIcon.value = currentTab.icon
            uiState.searchText.value = TextFieldValue(currentTab.title)
        }
    }

    // 效果：监听WebViewUrlState的变化，更新当前标签的URL
    LaunchedEffect(webViewUrlState.value) {
        if (currentTabIndex.intValue < tabs.size) {
            val currentTab = tabs[currentTabIndex.intValue]
            // 只有在URL真正变化时才更新
            if (currentTab.url != webViewUrlState.value) {
                currentTab.url = webViewUrlState.value
            }
        }
    }

    // 效果：处理URL加载
    LaunchedEffect(webViewUrlState.value, webViewState.value, loadState.isUrlFromSearch.value) {
        if (isSearchState.value || loadState.isFirstPageLoading.value) return@LaunchedEffect

        val webView = webViewState.value
        val url = webViewUrlState.value
        val currentTab = if (tabs.isNotEmpty() && currentTabIndex.intValue < tabs.size)
            tabs[currentTabIndex.intValue] else null

        if (webView != null && url.isNotEmpty() && currentTab != null && loadState.isWebViewReady.value) {
            // 检查当前WebView是否已加载此URL，避免重复加载
            val currentUrl = webView.url ?: ""
            if (loadState.isUrlFromSearch.value || (currentUrl != url && currentUrl.isEmpty())) {
                Log.d("Browser", "加载URL: $url")
                webView.loadUrl(url)
                currentTab.title = "正在加载中..."
                loadState.isUrlFromSearch.value = false
            }
        }
    }

    // 效果：更新搜索框显示的标题
    LaunchedEffect(currentTab.title) {
        if (!isSearchState.value) {
            uiState.searchText.value = TextFieldValue(currentTab.title)
        }
    }

    // 效果：搜索状态变化时更新搜索框
    LaunchedEffect(isSearchState.value) {
        if (isSearchState.value) {
            focusRequester.requestFocus()
            uiState.searchText.value = TextFieldValue("")
        } else {
            uiState.searchText.value = TextFieldValue(currentTab.title)
        }
    }

    // 处理返回键逻辑
    BackHandler {
        handleBackPress(
            isFieldFocused = uiState.isFieldFocused.value,
            focusManager = focusManager,
            isSearchState = isSearchState,
            webViewState = webViewState,
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
                        uiState.isFieldFocused.value = focusState.isFocused
                        if (focusState.isFocused) {
                            isSearchState.value = true
                        }
                    },
                searchText = uiState.searchText.value,
                focusRequester = focusRequester,
                onValueChange = {
                    uiState.searchText.value = it
                },
                onSearch = { tabOperations.performSearchOrNavigate(uiState.searchText.value.text) },
                webViewState = webViewState,
                isSearchState = isSearchState,
                siteIconState = uiState.currentTabIcon,
                isLoading = { currentTab.progress in 1..99 }
            )

            // 进度条
            if (shouldShowProgress) {
                LinearProgressIndicator(
                    progress = uiState.currentTabProgress.intValue / 100f
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
                        if (uiState.isFieldFocused.value) {
                            focusManager.clearFocus()
                        }
                    }
                    .padding(paddingValues)
            ) {
                // 搜索状态下显示历史记录
                if (isSearchState.value) {
                    RenderSearchHistoryPanel(
                        currentTab = remember { mutableStateOf(currentTab) },
                        historyList = historyList,
                        searchText = uiState.searchText,
                        performSearchOrNavigate = tabOperations.performSearchOrNavigate
                    )
                }

                // 为每个标签页创建WebView
                RenderTabWebViews(
                    tabs = tabs,
                    currentTabIndex = currentTabIndex.intValue,
                    isSearchState = isSearchState.value,
                    webViewState = webViewState,
                    webViewUrlState = webViewUrlState,
                    currentTabIconState = uiState.currentTabIcon,
                    currentTabProgressState = uiState.currentTabProgress,
                    searchText = uiState.searchText,
                    themeColor = themeColor,
                    webViewReady = loadState.isWebViewReady,
                    coroutineScope = coroutineScope
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
                    isMenuState = uiState.isMenuOpenState,
                    onCreateNewWebView = tabOperations.createNewTab,
                    tabCount = tabs.size,
                    isTabMenuState = uiState.isTabMenuOpenState,
                    onBack = {
                        handleBackPress(
                            isFieldFocused = false,
                            focusManager = focusManager,
                            isSearchState = isSearchState,
                            webViewState = webViewState,
                            tabs = tabs,
                            currentTabIndex = currentTabIndex,
                            navController = navController
                        )
                    }
                )
            }
        },
    )

    // 浏览器菜单
    BrowserMenu(uiState.isMenuOpenState)

    // 标签页管理界面
    BrowserTab(
        show = uiState.isTabMenuOpenState,
        listTab = tabs,
        onSelection = tabOperations.switchToTab,
        onClose = tabOperations.closeTab,
        onCreateNew = tabOperations.createNewTab,
        currentTabIndex = currentTabIndex.intValue
    )

    // 调试模式下显示FPS监视器
    AnimatedVisibility(visible = true) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .captionBarPadding()
                .padding(horizontal = 4.dp)
        )
    }
}


/**
 * 渲染搜索历史面板
 */
@Composable
private fun RenderSearchHistoryPanel(
    currentTab: MutableState<WebViewTabData>,
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
        if (currentTab.value.title.isNotEmpty()) {
            BrowserNowSiteInfo(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .padding(bottom = 10.dp),
                urlState = remember { mutableStateOf(currentTab.value.url) },
                titleState = remember { mutableStateOf(currentTab.value.title) },
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
    themeColor: Color,
    webViewReady: MutableState<Boolean>,
    coroutineScope: CoroutineScope
) {
    tabs.forEachIndexed { index, tab ->
        val isVisible = index == currentTabIndex && !isSearchState
        WebViewLayout(
            modifier = Modifier.fillMaxSize(),
            onTitleChange = { newTitle ->
                tab.title = newTitle
                if (isVisible) {
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
                if (loadedUrl.isNotEmpty()) {
                    tab.url = loadedUrl
                    if (index == currentTabIndex) {
                        // 只在当前标签页更新URL状态
                        webViewUrlState.value = loadedUrl
                    }
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

                    // 当页面加载完成时（100%）自动截图
                    if (newProgress == 100) {
                        captureWebViewSnapshot(tab, coroutineScope)
                    }
                }
            },
            onWebViewCreated = { webView ->
                tab.webView = webView
                if (index == currentTabIndex) {
                    webViewState.value = webView
                    // 标记WebView已准备好
                    webViewReady.value = true
                }
            },
            isVisible = isVisible
        )
    }
}


/**
 * 构建搜索或导航的URL
 */
private fun buildSearchOrNavigateUrl(query: String): String {
    return if (Patterns.WEB_URL.matcher(query).matches() || query.contains("://")) {
        if (!query.contains("://")) "https://$query" else query
    } else {
        "https://cn.bing.com/search?q=${Uri.encode(query)}"
    }
}

/**
 * 在当前标签页加载URL
 */
private fun loadUrlInCurrentTab(
    url: String,
    focusManager: FocusManager,
    isSearchState: MutableState<Boolean>,
    webViewUrlState: MutableState<String>,
    urlFromSearch: MutableState<Boolean>,
    currentTab: WebViewTabData
) {
    // 隐藏键盘，更新状态并加载URL
    focusManager.clearFocus()
    isSearchState.value = false
    webViewUrlState.value = url
    urlFromSearch.value = true

    // 立即加载URL
    currentTab.webView?.loadUrl(url)
    currentTab.url = url
    currentTab.title = "正在加载中..."
}

/**
 * 处理返回键逻辑
 */
private fun handleBackPress(
    isFieldFocused: Boolean,
    focusManager: FocusManager,
    isSearchState: MutableState<Boolean>,
    webViewState: MutableState<WebView?>,
    tabs: MutableList<WebViewTabData>,
    currentTabIndex: MutableState<Int>,
    navController: NavController
) {
    // 处理焦点状态
    if (isFieldFocused) {
        focusManager.clearFocus()
    }

    // 处理搜索状态
    if (isSearchState.value) {
        isSearchState.value = false
        if (!(tabs.size > 1 && currentTabIndex.value > 0)) {
            if (tabs[currentTabIndex.value].url != "") return
        }
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
        tabs.removeAll { true }
        // 返回主页
        navController.navigate("home") {
            popUpTo(0) { inclusive = false }
        }
    }
}

/**
 * 截取WebView快照，用于生成标签页预览
 */
private fun captureWebViewSnapshot(tab: WebViewTabData, coroutineScope: CoroutineScope) {
    val webView = tab.webView ?: return

    // 确保WebView已准备好并有内容
    if (webView.width <= 0 || webView.height <= 0) return

    coroutineScope.launch {
        withContext(Dispatchers.Main) {
            try {
                // 记录当前可见状态
                val wasVisible = webView.isVisible
                if (!wasVisible) {
                    webView.visibility = android.view.View.VISIBLE
                }

                // 创建适当大小的缩略图
                val scale = 0.25f // 缩小比例以提高性能
                val width = (webView.width * scale).toInt().coerceAtLeast(1)
                val height = (webView.height * scale).toInt().coerceAtLeast(1)

                val bitmap = createBitmap(width, height)
                val canvas = Canvas(bitmap)
                canvas.scale(scale, scale)

                // 绘制WebView内容到缩略图
                webView.draw(canvas)
                tab.thumbnail = bitmap

                // 还原之前的可见状态
                if (!wasVisible) {
                    webView.visibility = android.view.View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
