package com.xiaowine.winebrowser.webview

import android.webkit.WebChromeClient
import android.webkit.WebView

class WinewserWebChromeClient(val onTitleChange: (String) -> Unit) : WebChromeClient() {
    override fun onReceivedTitle(view: WebView?, title: String) {
        super.onReceivedTitle(view, title)
        onTitleChange(title)
    }
}