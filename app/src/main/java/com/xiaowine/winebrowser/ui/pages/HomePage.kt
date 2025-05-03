package com.xiaowine.winebrowser.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableState
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

/**
 * 主页预览函数
 */
@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun TestHomePage() {
    MiuixTheme {
        App(navController = rememberNavController())
    }
}

/**
 * 主页 Composable
 * 显示网页内容或起始页（包含搜索栏和快捷方式）
 *
 * @param navController 导航控制器
 * @param urlToLoad 可选的初始加载 URL。如果非空，则直接显示网页
 */
@SuppressLint("AutoboxingStateCreation", "SetJavaScriptEnabled")
@Composable
fun HomePage(
    navController: NavHostController,
    urlToLoad: String? = null
) {
    // 初始 URL，如果 urlToLoad 为空，则默认为 Bing 搜索
    val initialUrl = urlToLoad ?: "https://www.bing.com"
    // 当前 WebView 加载的 URL 状态
    var currentUrl by remember { mutableStateOf(initialUrl) }
    // 是否处于显示网页状态的状态变量
    var isInHtmlState by remember { mutableStateOf(urlToLoad != null) }
    // 网页标题状态
    var titleState by remember { mutableStateOf("") }
    // WebView 是否可以后退的状态
    var canGoBack by remember { mutableStateOf(false) }
    // WebView 是否可以前进的状态
    var canGoForward by remember { mutableStateOf(false) }
    // 示例状态，用于演示 rememberPreviewableState
    val historyList = rememberPreviewableState(
        realData = { AppConfig.title },
        previewData = "aaa",
        onSync = { AppConfig.title = it }
    )
    // WebView 实例状态
    var webViewInstance: WebView? by remember { mutableStateOf(null) }

    // 点击快捷方式的回调函数
    val onShortcutClick: (String) -> Unit = { url ->
        currentUrl = url // 更新当前 URL
        isInHtmlState = true // 切换到网页显示状态
    }

    // 处理返回按钮事件
    BackHandler(enabled = isInHtmlState) { // 仅在显示网页时启用
        if (canGoBack) {
            // 如果 WebView 可以后退，则执行后退操作
            webViewInstance?.goBack()
        } else {
            // 如果不能后退，则返回到起始页状态
            isInHtmlState = false
            titleState = "" // 清空标题
            canGoBack = false
            canGoForward = false
        }
    }

    Scaffold(
        topBar = {
            // 仅在显示网页时显示顶部的伪搜索栏
            AnimatedVisibility(
                visible = isInHtmlState
            ) {
                FakeSearchBar(navController, titleState.ifEmpty { currentUrl }) // 显示标题或 URL
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), // 应用 Scaffold 提供的内边距
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 根据 isInHtmlState 控制显示起始页内容或 WebView
                AnimatedVisibility(!isInHtmlState) { // 显示起始页
                    Column {
                        BigTitle() // 显示大标题
                        // 示例：显示和修改 historyList 状态
                        Text(
                            text = historyList.value,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    historyList.value = Date().toString() // 点击时更新状态
                                },
                            textAlign = TextAlign.Center
                        )
                        FakeSearchBar(navController) // 显示伪搜索栏
                        Shortcut(onShortcutClick = onShortcutClick) // 显示快捷方式
                    }
                }
                AnimatedVisibility(isInHtmlState) { // 显示网页
                    Web(
                        initialUrl = currentUrl, // 传递当前 URL
                        onWebViewReady = { webView -> webViewInstance = webView }, // 获取 WebView 实例
                        onTitleChange = { title -> titleState = title }, // 更新标题状态
                        onCanGoBackChange = { can -> canGoBack = can }, // 更新后退状态
                        onCanGoForwardChange = { can -> canGoForward = can } // 更新前进状态
                    )
                }
            }
        },
        bottomBar = {
            // 仅在显示网页时显示底部的按钮栏
            AnimatedVisibility(isInHtmlState) {
                ButtonBar(
                    canGoBack = canGoBack,
                    canGoForward = canGoForward,
                    onGoBack = { webViewInstance?.goBack() }, // 后退按钮点击事件
                    onGoForward = { webViewInstance?.goForward() } // 前进按钮点击事件
                )
            }
        },
        floatingToolbar = {
            // 仅在显示起始页时显示悬浮工具栏
            AnimatedVisibility(!isInHtmlState) {
                Toolbar()
            }
        },
        floatingToolbarPosition = ToolbarPosition.BottomEnd // 悬浮工具栏位置在右下角
    )

    // 仅在 Debug 模式下显示 FPS 监视器
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

/**
 * WebView Composable
 * 使用 AndroidView 嵌入原生 WebView
 *
 * @param initialUrl 初始加载的 URL
 * @param onWebViewReady WebView 实例准备好时的回调
 * @param onTitleChange 网页标题变化时的回调
 * @param onCanGoBackChange WebView 后退状态变化时的回调
 * @param onCanGoForwardChange WebView 前进状态变化时的回调
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Web(
    initialUrl: String,
    onWebViewReady: (WebView) -> Unit,
    onTitleChange: (String) -> Unit,
    onCanGoBackChange: (Boolean) -> Unit,
    onCanGoForwardChange: (Boolean) -> Unit
) {
    // 如果是预览模式，则不渲染 WebView
    if (AppConfig.isPreview) return
    // WebView 实例状态
    var webView: WebView? by remember { mutableStateOf(null) }

    // 当 initialUrl 变化时，重新加载 URL
    LaunchedEffect(initialUrl) {
        webView?.loadUrl(initialUrl)
    }

    AndroidView(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp)) // 页面边缘圆角裁剪
            .background(Color.Transparent) // 透明背景，避免闪烁
            .fillMaxSize(),
        factory = { ctx -> // 创建 WebView 实例
            // 启用 WebView 调试（仅 Debug 版本）
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            WebView(ctx).apply {
                //  启用硬件加速
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                // WebView 设置
                settings.apply {
                    allowContentAccess = true // 允许访问内容
                    allowFileAccess = true // 允许访问文件
                    builtInZoomControls = true // 启用内置缩放控件
                    databaseEnabled = true // 启用数据库存储
                    displayZoomControls = false // 不显示缩放控件
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // 允许混合内容
                    domStorageEnabled = true // 启用 DOM 存储
                    javaScriptCanOpenWindowsAutomatically = false // 禁止 JS 自动打开窗口
                    javaScriptEnabled = true // 启用 JavaScript
                    useWideViewPort = true // 使用宽视图
                    loadWithOverviewMode = true // 概览模式加载
                    mediaPlaybackRequiresUserGesture = false // 媒体播放不需要用户手势
                    scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY // 滚动条样式
                    layoutParams = ViewGroup.LayoutParams( // 设置布局参数
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setSupportZoom(true) // 支持缩放
                    setGeolocationEnabled(true) // 启用地理位置
                    setSupportMultipleWindows(true) // 支持多窗口
                }

                // 设置 WebChromeClient 处理 UI 相关事件
                webChromeClient = object : WebChromeClient() {
                    // 接收到网页标题时回调
                    override fun onReceivedTitle(view: WebView?, title: String?) {
                        super.onReceivedTitle(view, title)
                        title?.let { onTitleChange(it) } // 更新标题状态
                    }

                    // 页面加载进度变化时回调
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        // 更新前进后退状态
                        view?.let {
                            onCanGoBackChange(it.canGoBack())
                            onCanGoForwardChange(it.canGoForward())
                        }
                    }
                }
                // 设置 WebViewClient 处理页面加载和请求拦截
                webViewClient = object : WebViewClient() {
                    // 控制是否在 WebView 内加载 URL
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val uri = request.url
                        val scheme = uri.scheme
                        Log.d("WebView", "Loading URL: ${request.url}, Scheme: $scheme")
                        // 只允许 http 和 https 协议在 WebView 内加载
                        return !(scheme == "http" || scheme == "https")
                    }

                    // 页面加载完成时回调
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // 更新前进后退状态
                        view?.let {
                            onCanGoBackChange(it.canGoBack())
                            onCanGoForwardChange(it.canGoForward())
                        }
                    }

                    // TODO: 可以在这里屏蔽包含特定域名的广告请求？
                    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                        return super.shouldInterceptRequest(view, request)
                    }
                }

                // 允许第三方 Cookie
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                // 将创建的 WebView 实例赋值给状态变量
                webView = this
                // 回调通知 WebView 已准备好
                onWebViewReady(this)
            }
        },
        update = { updatedWebView ->
            // 更新 WebView 实例时的回调
            webView = updatedWebView
            onWebViewReady(updatedWebView)
            // 确保状态同步
            onCanGoBackChange(updatedWebView.canGoBack())
            onCanGoForwardChange(updatedWebView.canGoForward())
            Log.d("WebViewUpdate", "WebView updated. Current URL in state: $initialUrl")
        },
        onRelease = { releasedWebView ->
            // 销毁时释放 WebView
            releasedWebView.destroy()
            webView = null
            // 重置状态
            onCanGoBackChange(false)
            onCanGoForwardChange(false)
            onTitleChange("")
        }
    )
}

/**
 * 起始页的悬浮工具栏 Composable
 */
@Composable
fun Toolbar() {
    FloatingToolbar(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(8.dp),
        cornerRadius = 20.dp
    ) {
        Row {
            // 定义工具栏图标列表
            val iconsList = listOf(
                AddIcon, // 添加图标
                MenuIcon, // 菜单图标
            )
            iconsList.forEach {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(28.dp),
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = it,
                        contentDescription = "导航操作",
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

/**
 * 网页浏览时的底部按钮栏 Composable
 *
 * @param canGoBack 是否可以后退
 * @param canGoForward 是否可以前进
 * @param onGoBack 后退按钮点击回调
 * @param onGoForward 前进按钮点击回调
 */
@Composable
fun ButtonBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    onGoBack: () -> Unit,
    onGoForward: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 定义按钮图标和对应的点击事件
        val iconsList = listOf(
            ArrowLeftIcon to onGoBack, // 后退
            ArrowRightIcon to onGoForward, // 前进
            AddIcon to {}, // 添加（暂无功能）
            null to {}, // 标签页计数（暂无功能）
            MenuIcon to {}, // 菜单（暂无功能）
        )

        iconsList.forEachIndexed { index, (icon, action) ->
            // 根据索引判断按钮是否启用
            val isEnabled = when (index) {
                0 -> canGoBack // 后退按钮, 根据 canGoBack 状态启用
                1 -> canGoForward // 前进按钮, 根据 canGoForward 状态启用
                else -> true // 其他按钮, 暂未实现
            }
            // 根据启用状态设置透明度
            val alpha = if (isEnabled) 1f else 0.5f

            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp) // 垂直内边距
                    .size(32.dp) // 按钮大小
                    .alpha(alpha) // 应用透明度
                    .clickable(enabled = isEnabled) { action() }, // 添加点击事件，根据 isEnabled 控制是否响应
                contentAlignment = Alignment.Center
            ) {
                if (index == 3) { // 第四个位置显示标签页计数徽章
                    TabCountBadge(
                        count = 1, // 示例计数
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp)
                    )
                } else if (icon != null) { // 如果图标不为空，则显示图标
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        imageVector = icon,
                        contentDescription = "导航操作",
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

/**
 * 起始页的大标题 Composable
 */
@Composable
fun BigTitle() {
    val context: Context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = context.getString(R.string.app_name), // 从资源文件获取应用名称
            fontSize = 50.sp,
            fontWeight = FontWeight.W600,
            maxLines = 1 // 最多显示一行
        )
    }
}

/**
 * 伪搜索栏 Composable
 * 在起始页和网页顶部显示，点击后导航到搜索页
 *
 * @param navController 导航控制器
 * @param text 显示的文本，默认为提示语，在网页状态下可显示标题或 URL
 */
@Composable
fun FakeSearchBar(
    navController: NavController,
    text: String = "搜索或输入网址" // 默认提示文本
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
                .widthIn(max = 400.dp) // 限制最大宽度
                .fillMaxWidth()
                .height(55.dp)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = 2.dp,
                    color = MiuixTheme.colorScheme.onBackground,
                    shape = SmoothRoundedCornerShape(15.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // 禁用点击效果
                ) {
                    navController.navigate("search") // 导航到搜索页
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Search,
                    contentDescription = "搜索",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = text,
                    color = MiuixTheme.textStyles.main.color,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1, // 最多显示一行
                    overflow = TextOverflow.Ellipsis // 超出部分显示省略号
                )
            }
        }
    }
}

/**
 * 起始页的快捷方式区域 Composable
 *
 * @param onShortcutClick 点击快捷方式项的回调函数
 */
@Composable
fun Shortcut(onShortcutClick: (String) -> Unit) {
    // 定义快捷方式列表（名称和 URL）
    // TODO: 后续应改为用户自行配置
    val shortcuts = listOf(
        "百度" to "https://www.baidu.com",
        "哔哩哔哩" to "https://www.bilibili.com",
        "知乎" to "https://www.zhihu.com",
        "GitHub" to "https://github.com"
    )

    // 滚动状态
    var scrollState = rememberScrollState()
    // 自定义流式布局，用于排列快捷方式项
    FlowLayout(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(scrollState),
        horizontalSpacing = 8.dp,
        verticalSpacing = 16.dp
    ) {
        // 遍历快捷方式列表，为每个项创建 ShortcutItem
        shortcuts.forEach { (name, url) ->
            ShortcutItem(
                title = name,
                url = url,
                onClick = onShortcutClick // 传递点击回调
            )
        }
    }
}

/**
 * 单个快捷方式项 Composable
 *
 * @param title 快捷方式标题
 * @param url 快捷方式对应的 URL
 * @param onClick 点击时的回调函数
 */
@Composable
private fun ShortcutItem(
    title: String,
    url: String,
    onClick: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(SmoothRoundedCornerShape(12.dp))
                .background(MiuixTheme.colorScheme.dividerLine)
                .clickable { onClick(url) }, // 添加点击事件，回调时传递 URL
            contentAlignment = Alignment.Center
        ) {
            // 使用默认搜索图标
            // TODO: 后续可以根据 URL 自动加载网站图标或用户自定义图标
            Icon(
                imageVector = MiuixIcons.Basic.Search, // 搜索图标
                contentDescription = title, // 内容描述
                tint = MiuixTheme.colorScheme.onBackground, // 图标颜色
            )
        }
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

