package com.xiaowine.winebrowser.utils

import android.content.Context
import android.widget.Toast

object Utils {
    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}