package com.xiaowine.winebrowser.ui.component

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.utils.Utils
import com.xiaowine.winebrowser.webview.WinewserWebChromeClient
import com.xiaowine.winebrowser.webview.WinewserWebViewClient

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewLayout(
    modifier: Modifier = Modifier,
    onTitleChange: (String) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onIconChange: (Bitmap) -> Unit,
    onPageStarted: (String) -> Unit,
    onPageColorChange: (Int) -> Unit,
    onWebViewCreated: (WebView) -> Unit = {},
    webViewState: MutableState<WebView?>? = null,
    isVisible: Boolean = true
) {
    var webView: WebView? by remember { mutableStateOf(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // 处理生命周期事件
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                webView?.onPause()
            } else if (event == Lifecycle.Event.ON_RESUME) {
                webView?.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            webView?.destroy()
        }
    }

    if (Utils.isPreview) return

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

                // 配置 WebView 设置
                settings.apply {
                    allowContentAccess = true                                 // 允许访问内容
                    allowFileAccess = true                                    // 允许访问文件
                    builtInZoomControls = true                                // 启用内置缩放控件
                    displayZoomControls = false                               // 不显示缩放控件
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // 允许混合内容
                    domStorageEnabled = true                                  // 启用 DOM 存储
                    javaScriptCanOpenWindowsAutomatically = true              // JS 自动打开窗口
                    javaScriptEnabled = true                                  // 启用 JavaScript
                    useWideViewPort = true                                    // 使用宽视图
                    loadWithOverviewMode = true                               // 概览模式加载
                    mediaPlaybackRequiresUserGesture = false                  // 媒体播放不需手势
                    scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY        // 滚动条样式
                    setSupportZoom(true)                                      // 支持缩放
                    setGeolocationEnabled(true)                               // 启用地理位置
                    setSupportMultipleWindows(false)                          // 支持多窗口

                    // 可选的自定义 UserAgent
                    // userAgentString = "Mozilla/5.0 (Linux; Android 15)..."
                }

                // 设置 WebView 客户端
                webChromeClient = WinewserWebChromeClient(onTitleChange, onIconChange, onProgressChanged)
                webViewClient = WinewserWebViewClient(onPageStarted, onPageColorChange)

                // 配置 Cookie
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                // 更新状态并调用回调
                webView = this
                webViewState?.value = this
                onWebViewCreated(this)
            }
        },
        update = { updatedWebView ->
            // 根据 isVisible 参数控制 WebView 可见性
            updatedWebView.visibility = if (isVisible) ViewGroup.VISIBLE else ViewGroup.GONE

            // 如果WebView实例不同，更新状态
            if (webView != updatedWebView) {
                webView = updatedWebView
                webViewState?.value = updatedWebView
                onWebViewCreated(updatedWebView)
            }
        }
    )

    // 处理WebView的销毁
    DisposableEffect(Unit) {
        onDispose {
            webView?.let { wv ->
                wv.stopLoading()
                wv.destroy()
                webView = null
                webViewState?.value = null
            }
        }
    }
}
