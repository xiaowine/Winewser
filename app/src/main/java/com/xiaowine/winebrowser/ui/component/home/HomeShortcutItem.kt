package com.xiaowine.winebrowser.ui.component.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaowine.winebrowser.data.HomeShortcutItemData
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.theme.AppTheme
import com.xiaowine.winebrowser.utils.Utils.base64ToPainter
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun HomeShortcutItem(
    itemData: HomeShortcutItemData,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(SmoothRoundedCornerShape(12.dp))
                .background(AppTheme.colorScheme.homeShortBackgroundColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            when (itemData.base64Icon) {
                null -> {
                    Text(
                        text = itemData.title.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiuixTheme.colorScheme.onBackground
                    )
                }

                "" -> {
                    Icon(
                        imageVector = AddIcon,
                        modifier = Modifier.padding(10.dp),
                        contentDescription = itemData.title,
                        tint = AppTheme.colorScheme.iconTintColor
                    )
                }

                else -> {
                    val painter = base64ToPainter(itemData.base64Icon)
                    Icon(
                        painter = painter,
                        modifier = Modifier.padding(10.dp),
                        contentDescription = itemData.title,
                    )
                }
            }
        }
        Text(
            text = itemData.title,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

