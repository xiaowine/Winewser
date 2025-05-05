package com.xiaowine.winebrowser.ui.component.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaowine.winebrowser.R
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun HomeHeadline() {
    val context: Context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = context.getString(R.string.app_name),
            fontSize = 50.sp,
            fontWeight = FontWeight.W600
        )
    }
}
