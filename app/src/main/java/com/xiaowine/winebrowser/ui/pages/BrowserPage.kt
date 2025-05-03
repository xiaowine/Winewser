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
import androidx.compose.foundation.layout.captionBarPadding
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.ArrowLeftIcon
import com.xiaowine.winebrowser.ui.ArrowRightIcon
import com.xiaowine.winebrowser.ui.LinkIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.TabCountBadge
import com.xiaowine.winebrowser.ui.component.WebViewLayout
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Delete
import top.yukonga.miuix.kmp.icon.icons.useful.Like
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
fun BrowserPage(
    navController: NavController,
    urlToLoad: String? = "",
    isSearch: Boolean = false
) {
    // 状态管理
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val historyList = rememberPreviewableState(
        realData = { AppConfig.searchHistory },
        previewData = AppConfig.searchDefault,
        onSync = { AppConfig.searchHistory = it }
    )

    // 组件状态
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    var titleState by remember { mutableStateOf("") }
    var isSearchState by remember { mutableStateOf(isSearch) }
    var webViewUrlState = remember { mutableStateOf(urlToLoad ?: "") }
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    var isFieldFocused by remember { mutableStateOf(false) }

    // 监听WebView实例和URL变化，确保URL正确加载
    LaunchedEffect(webViewState.value, webViewUrlState.value) {
        if (isSearchState) return@LaunchedEffect
        val webView = webViewState.value
        val url = webViewUrlState.value

        if (webView != null && url.isNotEmpty()) {
            Log.d("Browser", "Loading URL: $url")
            webView.loadUrl(url)
        }
    }

    // 更新搜索框显示的标题
    LaunchedEffect(titleState) {
        searchText = TextFieldValue(titleState)
    }

    LaunchedEffect(isSearchState) {
        if (isSearchState) focusRequester.requestFocus()
    }

    // 执行搜索或导航的 Lambda 函数
    val performSearchOrNavigate: (String) -> Unit = { query ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isNotEmpty()) {
            // 确定最终加载的URL
            val url = if (Patterns.WEB_URL.matcher(trimmedQuery).matches() || trimmedQuery.contains("://")) {
                if (!trimmedQuery.contains("://")) "https://$trimmedQuery" else trimmedQuery
            } else {
                "https://www.bing.com/search?q=${Uri.encode(trimmedQuery)}"
            }

            // 更新搜索历史记录
            val currentList = historyList.value.toMutableList()
            currentList.remove(trimmedQuery)
            currentList.add(0, trimmedQuery)

            if (currentList.size > 20) {
                historyList.value = currentList.take(20)
            } else {
                historyList.value = currentList
            }

            // 隐藏键盘，更新状态并加载URL
            focusManager.clearFocus()
            isSearchState = false
            webViewUrlState.value = url
        }
    }

    // 处理返回键逻辑
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
            // 搜索栏
            SearchField(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 6.dp)
                    .onFocusChanged { focusState ->
                        isFieldFocused = focusState.isFocused
                        if (focusState.isFocused) {
                            isSearchState = true
                        }
                    },
                searchText = searchText,
                focusRequester = focusRequester,
                onValueChange = { searchText = it },
                onSearch = { performSearchOrNavigate(searchText.text) }
            )

            // 加载进度条
            if (progress != 100 && !isSearchState) {
                LinearProgressIndicator(progress = progress / 100f)
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
            ) {
                // 内容区域
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {

                    // 搜索状态下显示历史记录，使用Box包裹以防止点击事件穿透
                    if (isSearchState) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MiuixTheme.colorScheme.background)
                        ) {
                            SearchHistoryPanel(
                                modifier = Modifier.fillMaxSize(),
                                historyList = historyList.value,
                                onSelected = { performSearchOrNavigate(it) }
                            )
                        }
                    }
                    // WebView - 始终渲染，但根据搜索状态控制可见性
                    WebViewLayout(
                        modifier = Modifier.fillMaxSize(),
                        onTitleChange = { titleState = it },
                        onPageStarted = { loadedUrl ->
                            if (loadedUrl.isNotEmpty() && loadedUrl != webViewUrlState.value) {
                                webViewUrlState.value = loadedUrl
                            }
                        },
                        onProgressChanged = { progress = it },
                        webViewState = webViewState,
                        isVisible = !isSearchState
                    )

                }
            }
        },
        bottomBar = {
            // 非搜索状态下显示底部导航栏
            if (!isSearchState) {
                WebViewButtonBar(
                    navController,
                    webViewState,
                    webViewUrlState
                )
            }
        },
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
fun WebViewButtonBar(
    navController: NavController,
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
                    .clickable(enabled = (i == 0) || (i == 1 && canGoForward.value)) {
                        if (i == 0) {
                            if (webView?.canGoBack() == true) {
                                webView.goBack()
                            } else {
                                navController.navigate("home") {
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        label = "搜索或输入网址",
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
fun SearchHistoryPanel(
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
                val cleanItem = item.replace("\n", "")
                val isLink = Patterns.WEB_URL.matcher(cleanItem).matches() || cleanItem.contains("://")
                Row(
                    modifier = Modifier
                        .clip(SmoothRoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.dividerLine)
                        .clickable { onSelected(item) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isLink) {
                        Icon(
                            modifier = Modifier
                                .rotate(90f)
                                .size(20.dp),
                            imageVector = LinkIcon,
                            contentDescription = "链接"
                        )
                    }
                    Text(
                        maxLines = 1,
                        text = cleanItem,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
