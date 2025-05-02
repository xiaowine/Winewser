package com.xiaowine.winebrowser.ui.component


import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.webview.WinewserWebChromeClient
import com.xiaowine.winebrowser.webview.WinewserWebViewClient

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun Web(
    onTitleChange: (String) -> Unit,
    onPageStarted: (String) -> Unit,
    webViewState: MutableState<WebView?> // 新增参数
) {
    if (AppConfig.isPreview) return
//    val url = "https://m.bilibili.com/"
    val url = "https://www.limestart.cn/"
    AndroidView(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Red)
            .fillMaxSize(),
        factory = { ctx ->
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
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
//                    userAgentString = "Mozilla/5.0 (Linux; Android 15; 22127RK46C Build/AQ3A.240912.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/135.0.7049.110 Mobile Safari/537.36"

                }
                webChromeClient = WinewserWebChromeClient(onTitleChange)
                webViewClient = WinewserWebViewClient(onPageStarted)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                loadUrl(url)
                webViewState.value = this // 绑定WebView实例
            }
        },
    )
}
