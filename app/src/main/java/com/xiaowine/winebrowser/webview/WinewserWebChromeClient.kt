package com.xiaowine.winebrowser.webview

import android.webkit.WebChromeClient
import android.webkit.WebView

class WinewserWebChromeClient(
    val onTitleChange: (String) -> Unit,
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
}