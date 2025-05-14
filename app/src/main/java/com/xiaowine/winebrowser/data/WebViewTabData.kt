package com.xiaowine.winebrowser.data

import android.graphics.Bitmap
import android.graphics.Color
import android.webkit.WebView
import java.util.UUID

/**
 * 表示浏览器中的一个标签页
 *
 * @property id 标签页的唯一标识符
 * @property webView 与标签页关联的WebView实例
 * @property url 当前加载的URL
 * @property title 页面标题
 * @property icon 页面图标
 * @property themeColor 主题颜色
 * @property progress 页面加载进度（0-100）
 */
data class WebViewTabData(
    val id: String = UUID.randomUUID().toString(),
    var webView: WebView? = null,
    var url: String = "",
    var title: String = "",
    var icon: Bitmap? = null,
    var themeColor: Int = Color.WHITE,
    var progress: Int = 0
)