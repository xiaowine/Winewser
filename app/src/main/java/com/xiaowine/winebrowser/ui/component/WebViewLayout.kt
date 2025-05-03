package com.xiaowine.winebrowser.ui.component

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.webview.WinewserWebChromeClient
import com.xiaowine.winebrowser.webview.WinewserWebViewClient

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewLayout(
    modifier: Modifier = Modifier,
    onTitleChange: (String) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onPageStarted: (String) -> Unit,
    webViewState: MutableState<WebView?>,
    isVisible: Boolean = true
) {
    var webView: WebView? by remember { mutableStateOf(null) }

    if (AppConfig.isPreview) return
    
    AndroidView(
        modifier = modifier.background(Color.Transparent),
        factory = { ctx ->
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                // 启用硬件加速
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                
                // 配置WebView设置
                settings.apply {
                    allowContentAccess = true                               // 允许访问内容
                    allowFileAccess = true                                  // 允许访问文件
                    builtInZoomControls = true                              // 启用内置缩放控件
                    databaseEnabled = true                                  // 启用数据库存储
                    displayZoomControls = false                             // 不显示缩放控件
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // 允许混合内容
                    domStorageEnabled = true                                // 启用DOM存储
                    javaScriptCanOpenWindowsAutomatically = true            // JS自动打开窗口
                    javaScriptEnabled = true                                // 启用JavaScript
                    useWideViewPort = true                                  // 使用宽视图
                    loadWithOverviewMode = true                             // 概览模式加载
                    mediaPlaybackRequiresUserGesture = false                // 媒体播放不需手势
                    scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY      // 滚动条样式
                    setSupportZoom(true)                                    // 支持缩放
                    setGeolocationEnabled(true)                             // 启用地理位置
                    setSupportMultipleWindows(false)                        // 支持多窗口
                    
                    // 可选的自定义UserAgent
                    // userAgentString = "Mozilla/5.0 (Linux; Android 15)..."
                }
                
                // 设置WebView客户端
                webChromeClient = WinewserWebChromeClient(onTitleChange, onProgressChanged)
                webViewClient = WinewserWebViewClient(onPageStarted)
                
                // 配置Cookie
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                
                // 更新状态
                webViewState.value = this
                webView = this
            }
        },
        update = { updatedWebView ->
            // 根据isVisible参数控制WebView的可见性
            updatedWebView.visibility = if (isVisible) ViewGroup.VISIBLE else ViewGroup.INVISIBLE
            webView = updatedWebView
        },
        onRelease = { releasedWebView ->
            // WebView销毁时的清理工作
            releasedWebView.destroy()
            webView = null
            onTitleChange("")
        }
    )
}
