package com.xiaowine.winebrowser.webview

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebView

class WinewserWebChromeClient(
    val onTitleChange: (String) -> Unit,
    val onIconChange: (Bitmap) -> Unit,
    val onProgressChanged: (Int) -> Unit,
) : WebChromeClient() {
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        title?.let { onTitleChange(it) }
    }

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        onProgressChanged(newProgress)
    }

    override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
        icon?.let { onIconChange(it) }
    }
}