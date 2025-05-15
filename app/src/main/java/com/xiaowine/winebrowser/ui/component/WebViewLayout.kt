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
    val webViewInitialized = remember { mutableStateOf(false) }

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

                    // 缓存设置以提高页面恢复速度
                    cacheMode = WebSettings.LOAD_DEFAULT
                    databaseEnabled = true

                    // 优化加载速度
                    loadsImagesAutomatically = true
                }

                // 设置 WebView 客户端
                webChromeClient = WinewserWebChromeClient(
                    onTitleChange = { title ->
//                        Log.d("WebViewLayout", "[$instanceId] 标题变更: $title")
                        onTitleChange(title)
                    },
                    onIconChange = onIconChange,
                    onProgressChanged = { progress ->
//                        Log.d("WebViewLayout", "[$instanceId] 进度变更: $progress")
                        onProgressChanged(progress)
                    }
                )
                webViewClient = WinewserWebViewClient(
                    onPageStarted = { url ->
//                        Log.d("WebViewLayout", "[$instanceId] 页面加载开始: $url")
                        onPageStarted(url)
                    },
                    onPageColorChange = onPageColorChange
                )

                // 配置 Cookie
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                // 确保WebView完全初始化
                post {
                    // 标记WebView已初始化
                    webViewInitialized.value = true

                    // 更新状态并调用回调
                    webView = this
                    webViewState?.value = this

                    // 设置可见性
                    visibility = if (isVisible) ViewGroup.VISIBLE else ViewGroup.GONE

                    onWebViewCreated(this)
                } // 给WebView一点时间初始化
            }
        },
        update = { updatedWebView ->
            // 已存在WebView实例，只需更新可见性
            if (webViewInitialized.value) {
                updatedWebView.visibility = if (isVisible) ViewGroup.VISIBLE else ViewGroup.GONE

                // 确保webViewState中的值是最新的
                webViewState?.value = updatedWebView
            }
        }
    )

    // 当组件移除时，处理WebView资源
    DisposableEffect(Unit) {
        onDispose {
            // 注意：我们不会销毁WebView，只是停止其加载，保持其状态
            webView?.let { wv ->
                if (!isVisible) {
                    wv.stopLoading() // 如果当前不可见，可以停止加载
                }
            }
        }
    }
}
