package com.xiaowine.winebrowser.webview

import android.app.AlertDialog
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.xiaowine.winebrowser.utils.Utils.showToast

class WinewserWebViewClient(val onPageStarted: (String) -> Unit) : WebViewClient() {
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
//                                val chooser = Intent.createChooser(intent, "选择应用打开")
//                                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
//                                view.context.startActivity(chooser)
                return true
            } else {
                view.context.showToast("没有可用的应用来打开此链接")
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }

    // 添加doUpdateVisitedHistory监听，捕获前进后退等操作
    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        if (!isReload && url != null) {
            onPageStarted(url)
        }
    }
}