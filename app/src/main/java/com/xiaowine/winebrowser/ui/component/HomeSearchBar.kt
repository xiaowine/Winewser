package com.xiaowine.winebrowser.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun HomeSearchBar(
    navController: NavController,
    text: String = "搜索或输入网址"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .padding(horizontal = 24.dp)
                .padding(bottom = 5.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(55.dp)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = 2.dp,
                    color = MiuixTheme.colorScheme.onBackground,
                    shape = SmoothRoundedCornerShape(15.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // 禁用点击效果
                ) {
                    navController.navigate("browser?url=\"\"&isSearch=true")
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Search,
                    contentDescription = "Search",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = text,
                    color = MiuixTheme.textStyles.main.color,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // 超出部分显示省略号
                )
            }
        }
    }
}