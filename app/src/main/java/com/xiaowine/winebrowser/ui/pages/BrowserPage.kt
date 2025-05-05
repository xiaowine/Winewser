package com.xiaowine.winebrowser.ui.pages

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
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
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.config.AppConfig
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.ArrowLeftIcon
import com.xiaowine.winebrowser.ui.ArrowRightIcon
import com.xiaowine.winebrowser.ui.LinkIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.BrowserMenu
import com.xiaowine.winebrowser.ui.component.BrowserTabCountBadge
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.WebViewLayout
import com.xiaowine.winebrowser.ui.theme.AppTheme
import com.xiaowine.winebrowser.utils.ConfigUtils.rememberPreviewableState
import com.xiaowine.winebrowser.utils.Utils.copyToClipboard
import com.xiaowine.winebrowser.utils.Utils.showToast
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconDefaults
import top.yukonga.miuix.kmp.basic.LinearProgressIndicator
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Copy
import top.yukonga.miuix.kmp.icon.icons.useful.Edit
import top.yukonga.miuix.kmp.icon.icons.useful.NavigatorSwitch
import top.yukonga.miuix.kmp.icon.icons.useful.Refresh
import top.yukonga.miuix.kmp.icon.icons.useful.Search
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
    urlToLoad: String?,
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
    var siteTitleState = remember { mutableStateOf("") }
    var siteIconState = remember { mutableStateOf<Bitmap?>(null) }
    var siteColorState = remember { mutableIntStateOf(Color.WHITE) }

    var searchText = remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    var isSearchState = remember { mutableStateOf(isSearch) }
    var isMenuState = remember { mutableStateOf(false) }
    var webViewUrlState = remember { mutableStateOf(urlToLoad ?: "") }
    val webViewState = remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
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
                                androidx.compose.ui.graphics.Color(siteColorState.intValue)
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
                    siteIconState = siteIconState
                )

                // 加载进度条
                if (progress != 100 && !isSearchState.value) {
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
                        if (isSearchState.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MiuixTheme.colorScheme.background)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (siteTitleState.value.isNotEmpty()) {
                                        BrowserNowSiteInfo(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .padding(top = 16.dp)
                                                .padding(bottom = 10.dp),
                                            urlState = webViewUrlState,
                                            titleState = siteTitleState,
                                            searchText = searchText
                                        )
                                    }
                                    BrowserSearchHistoryPanel(
                                        modifier = Modifier.fillMaxSize(),
                                        historyList = historyList.value,
                                        onSelected = { performSearchOrNavigate(it) }
                                    )
                                }
                            }
                        }
                        WebViewLayout(
                            modifier = Modifier.fillMaxSize(),
                            onTitleChange = { siteTitleState.value = it },
                            onIconChange = {
                                siteIconState.value = it
                            },
                            onPageStarted = { loadedUrl ->
                                if (loadedUrl.isNotEmpty() && loadedUrl != webViewUrlState.value && !urlFromSearch) {
                                    webViewUrlState.value = loadedUrl
                                } else {
                                    urlFromSearch = false
                                }
                            },
                            onPageColorChange = {
                                siteColorState.intValue = it
                            },
                            onProgressChanged = { progress = it },
                            webViewState = webViewState,
                            isVisible = !isSearchState.value
                        )
                    }
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
                                    androidx.compose.ui.graphics.Color(siteColorState.intValue)
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

@Composable
fun BrowserButtonBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    webViewState: MutableState<WebView?>,
    webViewUrlState: MutableState<String>,
    isMenuState: MutableState<Boolean>,
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
                    .clickable {
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
fun BrowserSearchField(
    modifier: Modifier = Modifier,
    searchText: TextFieldValue,
    focusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    webViewState: MutableState<WebView?>,
    isSearchState: MutableState<Boolean>,
    siteIconState: MutableState<Bitmap?>,
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
                color = AppTheme.colorScheme.homeSearchLineColor,
                shape = SmoothRoundedCornerShape(15.dp)
            )
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            if (searchText.text.trim().isNotEmpty()) {
                onSearch()
            }
        }),
        label = "搜索或输入网址",
        leadingIcon = {
            val modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(24.dp)
            if (!isSearchState.value) {
                if (siteIconState.value != null) {
                    Image(
                        modifier = modifier,
                        painter = BitmapPainter(siteIconState.value!!.asImageBitmap()),
                        contentDescription = null,
                    )
                } else {
                    Icon(
                        modifier = modifier
                            .clickable(
                                indication = null,
                                interactionSource = null
                            ) {},
                        imageVector = MiuixIcons.Useful.NavigatorSwitch,
                        contentDescription = "Search",
                        tint = AppTheme.colorScheme.iconTintColor
                    )
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        if (isSearchState.value) {
                            onSearch()
                        } else {
                            webViewState.value!!.reload()
                        }
                    },
                imageVector = if (isSearchState.value) MiuixIcons.Useful.Search else MiuixIcons.Useful.Refresh,
                contentDescription = "Search",
                tint = if (searchText.text.trim().isEmpty()) {
                    AppTheme.colorScheme.iconTintColor.copy(alpha = 0.3f)
                } else {
                    AppTheme.colorScheme.iconTintColor
                }
            )
        }
    )
}

@Composable
fun BrowserNowSiteInfo(
    modifier: Modifier = Modifier,
    urlState: MutableState<String>,
    titleState: MutableState<String>,
    searchText: MutableState<TextFieldValue>
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    maxLines = 1,
                    text = titleState.value,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    maxLines = 1,
                    text = urlState.value,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        context.copyToClipboard("URL", urlState.value)
                        context.showToast("已复制链接")
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = MiuixIcons.Useful.Copy,
                        contentDescription = "复制",
                        tint = AppTheme.colorScheme.iconTintColor
                    )
                    Text(
                        text = "复制",
                        fontSize = 14.sp,
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            searchText.value = TextFieldValue(urlState.value)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = MiuixIcons.Useful.Edit,
                        contentDescription = "编辑",
                        tint = AppTheme.colorScheme.iconTintColor
                    )
                    Text(
                        text = "编辑",
                        fontSize = 14.sp,
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun BrowserSearchHistoryPanel(
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
                        .background(AppTheme.colorScheme.searchHistoryBackgroundColor)
                        .clickable { onSelected(item) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isLink) {
                        Icon(
                            modifier = Modifier
                                .rotate(90f)
                                .size(20.dp),
                            imageVector = LinkIcon,
                            contentDescription = "链接",
                            tint = AppTheme.colorScheme.iconTintColor
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
