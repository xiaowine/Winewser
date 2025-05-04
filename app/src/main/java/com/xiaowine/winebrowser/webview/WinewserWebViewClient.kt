package com.xiaowine.winebrowser.webview

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get

class WinewserWebViewClient(
    val onPageStarted: (String) -> Unit,
    val onPageColorChange: (Int) -> Unit,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val uri = request.url
        val scheme = uri.scheme // 获取 Scheme（如 http, https, bilibili, tel 等）

        // 如果是http或https链接，让WebView正常加载
        if (scheme == "http" || scheme == "https") {
            return false
        }

        // 对于其他scheme（如bilibili://, tel://, mailto:等），尝试打开本地应用
        try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val packageManager = view.context.packageManager
            val resolveInfo = packageManager.queryIntentActivities(intent, 0)
            if (resolveInfo.isNotEmpty()) {
                AlertDialog.Builder(view.context)
                    .setMessage("是否外部打开应用")
                    .setPositiveButton("确定") { _, _ ->
                        // 通过Intent打开链接
                        val chooser = Intent.createChooser(intent, "选择应用打开")
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                        view.context.startActivity(chooser)
                    }
                    .setNegativeButton("取消", null)
                    .show()
                return true
            } else {
//                view.context.showToast("没有可用的应用来打开此链接")
                return true
            }
        } catch (_: Exception) {
//            e.printStackTrace()
            return true
        }
    }

    // 添加doUpdateVisitedHistory监听，捕获前进后退等操作
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        if (!isReload && url != null) {
            onPageStarted(url)
        }
        view?.post {
            try {
                // 创建一个与顶部区域相同大小的位图
                val bitmap = createBitmap(view.width, 20)
                // 将WebView顶部区域绘制到位图
                view.draw(android.graphics.Canvas(bitmap))

                // 用Map统计颜色频率
                val colorMap = mutableMapOf<Int, Int>()
                val totalPixels = bitmap.width * bitmap.height

                // 遍历所有像素
                for (x in 0 until bitmap.width) {
                    for (y in 0 until bitmap.height) {
                        val color = bitmap[x, y]
                        colorMap[color] = (colorMap[color] ?: 0) + 1
                    }
                }

                // 找出出现次数最多的颜色
                val maxEntry = colorMap.maxByOrNull { it.value }
                val dominantColor = if (maxEntry != null) {
                    // 计算最多颜色的占比
                    val percentage = (maxEntry.value.toFloat() / totalPixels) * 100
                    // 如果占比超过30%才使用该颜色，否则使用默认颜色
                    if (percentage > 30) {
                        maxEntry.key
                    } else {
                        Color.WHITE // 默认颜色
                    }
                } else {
                    Color.WHITE // 默认颜色
                }

                onPageColorChange(dominantColor)

                // 回收位图
                bitmap.recycle()
            } catch (e: Exception) {
                android.util.Log.e("WebView", "Error getting dominant color", e)
            }
        }
    }

    // TODO: 可以在这里屏蔽包含特定域名的广告请求？
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

}