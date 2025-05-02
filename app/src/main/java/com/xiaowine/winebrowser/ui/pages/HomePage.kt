package com.xiaowine.winebrowser.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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

    LocalContext.current
    Scaffold(
        topBar = {
            AnimatedVisibility(isInHtmlState) {
                FakeSearchBar(navController, titleState)
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(!isInHtmlState) {
                    BigTitle()
                    Text(
                        historyList.value,
                        modifier = Modifier.clickable {
                            historyList.value = Date().toString()
                        })
                    FakeSearchBar(navController)
                    Shortcut()
                }
                AnimatedVisibility(isInHtmlState) {
                    Web {
                        titleState = it
                    }
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(isInHtmlState) {
                ButtonBar()
            }
        },
        floatingToolbar = {
            AnimatedVisibility(!isInHtmlState) {
                Toolbar()
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Web(onTitleChange: (String) -> Unit) {
    if (AppConfig.isPreview) return
//    val url = "https://www.limestart.cn/"
    val url = "https://www.douyin.com/?is_from_mobile_home=1/"
//    val url = "https://m.bilibili.com/"
    AndroidView(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Red)
            .fillMaxSize(),
        factory = { ctx ->
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            WebView(ctx).apply {
                settings.apply {
// 允许 WebView 访问内容提供者
                    allowContentAccess = true
// 允许 WebView 访问文件
                    allowFileAccess = true
// 启用内置缩放控件
                    builtInZoomControls = true
// 启用数据库存储 API
                    databaseEnabled = true
// 隐藏缩放控件
                    displayZoomControls = false
// 允许混合内容（http/https 混用）
                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
// 启用 DOM 存储 API
                    domStorageEnabled = true
// 禁止 JS 自动打开新窗口
                    javaScriptCanOpenWindowsAutomatically = false
// 启用 JavaScript
                    javaScriptEnabled = true
// 加载页面时自适应屏幕
                    loadWithOverviewMode = true
// 媒体播放不需要用户手势
                    mediaPlaybackRequiresUserGesture = false
// 支持视口属性
                    useWideViewPort = true
// 支持缩放
                    setSupportZoom(true)
// 启用地理定位
                    setGeolocationEnabled(true)
// 支持多窗口
                    setSupportMultipleWindows(true)
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onReceivedTitle(view: WebView?, title: String) {
                        super.onReceivedTitle(view, title)
                        onTitleChange(title)
                    }
                }
                webViewClient = object : WebViewClient() {
                    // 处理 Android 5.0+ (API 21+)
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val uri = request.url
                        val scheme = uri.scheme // 获取 Scheme（如 http, https, bilibili, tel 等）
                        return !(scheme == "http" || scheme == "https") // 只允许 http 和 https 链接
                    }
                }

                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                loadUrl(url)
            }
        },
    )
}

@Composable
fun Toolbar() {
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
fun ButtonBar() {
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

        for (i in 0 until 5) {

//        iconsList.forEach {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(32.dp),
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
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = iconsList[i]!!,
                        contentDescription = "Navigation action",
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        }
//    }
    }
}

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
            text = context.getString(R.string.app_name), fontSize = 50.sp, fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun FakeSearchBar(
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
                    text = text, color = MiuixTheme.textStyles.main.color, modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Shortcut() {
//    val shortcuts = listOf("百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩")
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
            ShortcutItem(
                title = item, onClick = { current.showToast(item) })
        }
    }
}

@Composable
private fun ShortcutItem(
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

