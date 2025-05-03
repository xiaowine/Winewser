package com.xiaowine.winebrowser.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

/**
 * 标签页数量图标组件
 *
 * @param count 要显示的标签页数量
 * @param modifier 修饰符
 * @param textColor 文本颜色
 * @param borderColor 边框颜色
 * @param backgroundColor 背景颜色(默认透明)
 */
@Composable
fun BrowserTabCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    textColor: Color = MiuixTheme.colorScheme.onBackground,
    borderColor: Color = MiuixTheme.colorScheme.onBackground,
    backgroundColor: Color = Color.Transparent
) {
    val displayText = when {
        count > 99 -> "99+"
        else -> count.toString()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = SmoothRoundedCornerShape(4.dp)
            )
    ) {
        Text(
            text = displayText,
            color = textColor,
            fontSize = when {
                count > 99 -> 8.sp
                count > 9 -> 10.sp
                else -> 12.sp
            },
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}