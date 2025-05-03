package com.xiaowine.winebrowser.ui.pages

import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.ArrowLeftIcon
import com.xiaowine.winebrowser.ui.ArrowRightIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.TabCountBadge
import com.xiaowine.winebrowser.ui.component.Web
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Delete
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
fun TestBrowser() {
    MiuixTheme {
        App("browser?url=&isSearch=true")
    }
}


@Composable
fun Browser(
    navController: NavController,
    urlToLoad: String? = "",
    isSearch: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val current = LocalContext.current

    val historyList = rememberPreviewableState(
        realData = { AppConfig.searchHistory },
        previewData = listOf("百度", "知乎", "B站"),
        onSync = { AppConfig.searchHistory = it }
    )

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    var titleState by remember { mutableStateOf("") }
    var isSearchState by remember { mutableStateOf(isSearch) }
    var webViewUrlState = remember { mutableStateOf(urlToLoad ?: "") }
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }


    // 监听WebView实例和URL变化，确保URL正确加载
    LaunchedEffect(webViewState.value, webViewUrlState.value) {
        val webView = webViewState.value
        val url = webViewUrlState.value

        if (webView != null && url.isNotEmpty()) {
            Log.d("Browser", "Loading URL: $url")
            webView.loadUrl(url)
        }
    }
    LaunchedEffect(titleState) {
        searchText = TextFieldValue(titleState)
    }

// 执行搜索或导航的 Lambda 函数
    val performSearchOrNavigate: (String) -> Unit = { query ->
        val trimmedQuery = query.trim() // 去除首尾空格
        if (trimmedQuery.isNotEmpty()) {
            // 判断输入是 URL 还是搜索词
            val url = if (Patterns.WEB_URL.matcher(trimmedQuery).matches() || trimmedQuery.contains("://")) {
                // 如果是 URL 但不包含协议头，则添加 https://
                if (!trimmedQuery.contains("://")) "https://$trimmedQuery" else trimmedQuery
            } else {
                // 如果是搜索词，则使用 Bing 搜索引擎进行搜索
                "https://www.bing.com/search?q=${Uri.encode(trimmedQuery)}"
            }

            // 将查询添加到历史记录中，避免重复项，并保持最新的在最前面
            val currentList = historyList.value.toMutableList()
            // 如果已存在，先移除旧的
            currentList.remove(trimmedQuery)
            // 然后添加到开头
            currentList.add(0, trimmedQuery)
            // 限制历史记录数量，保留最近的20条
            if (currentList.size > 20) {
                historyList.value = currentList.take(20)
            } else {
                historyList.value = currentList
            }

            // 清除焦点，隐藏键盘
            focusManager.clearFocus()
            // 更新状态并加载 URL
            isSearchState = false
            // 更新URL状态以触发URL加载
            webViewUrlState.value = url
        }
    }

    BackHandler {
        if (isSearchState) {
            isSearchState = false
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

    Scaffold(
        topBar = {
            SearchField(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            isSearchState = true
                        }
                    },
                searchText = searchText,
                focusRequester = focusRequester,
                onValueChange = { searchText = it }, // 更新搜索文本状态
                onSearch = { performSearchOrNavigate(searchText.text) } // 执行搜索
            )
            if (progress != 100) {
                LinearProgressIndicator(progress = progress / 100f)
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues).padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSearchState) {
                    HistoryItem(
                        modifier = Modifier.fillMaxSize(),
                        historyList = historyList.value,
                        onSelected = { performSearchOrNavigate(it) } // 点击历史记录项执行搜索
                    )
                } else {
                    Web(
                        modifier = Modifier.fillMaxSize(),
                        onTitleChange = { titleState = it },
                        onPageStarted = { loadedUrl ->
                            // 当页面开始加载时更新URL状态，但不要再次触发加载
                            if (loadedUrl.isNotEmpty() && loadedUrl != webViewUrlState.value) {
                                webViewUrlState.value = loadedUrl
                            }
                        },
                        onProgressChanged = {
                            progress = it
                        },
                        webViewState = webViewState
                    )
                }
            }
        },
        bottomBar = {
            if (!isSearchState) {
                WebViewButtonBar(
                    navController,
                    webViewState,
                    webViewUrlState
                )
            }
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


@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    searchText: TextFieldValue,
    focusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = {
            val filteredText = it.copy(text = it.text.replace("\n", ""))
            onValueChange(filteredText)
        },
        useLabelAsPlaceholder = true,
        cornerRadius = 15.dp,
        backgroundColor = MiuixTheme.colorScheme.background,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .border(
                width = 2.dp,
                color = MiuixTheme.colorScheme.onBackground,
                shape = SmoothRoundedCornerShape(15.dp)
            )
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), // 设置键盘动作为搜索
        keyboardActions = KeyboardActions(onSearch = { // 处理键盘搜索动作
            onSearch()
        }),
        label = "搜索或输入网址", // 标签文本
        trailingIcon = {
            if (searchText.text.isNotEmpty()) {
                IconButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onValueChange(TextFieldValue("")) }
                ) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Delete,
                        contentDescription = "清除"
                    )
                }
            }
        }
    )
}

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    historyList: List<String>,
    onSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Text("历史记录", style = MiuixTheme.textStyles.main)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            historyList.forEach { item ->
                Box(
                    modifier = Modifier
                        .clip(SmoothRoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.dividerLine)
                        .clickable { onSelected(item) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val cleanItem = item.replace("\n", "")
                    Text(
                        maxLines = 1,
                        // 限制最大长度，超出部分显示省略号
                        text = if (cleanItem.length > 20) "${cleanItem.take(20)}..." else cleanItem,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

