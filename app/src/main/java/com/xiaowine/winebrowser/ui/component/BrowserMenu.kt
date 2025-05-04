package com.xiaowine.winebrowser.ui.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.icon.icons.useful.Copy
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun BrowserMenu(
    isMenuState: MutableState<Boolean>
) {


    AnimatedVisibility(
        visible = isMenuState.value,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.onBackground.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    isMenuState.value = !isMenuState.value
                }
        ) {
            FlowLayout(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 400.dp)
                    .background(
                        color = MiuixTheme.colorScheme.background,
                        shape = SmoothRoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
                    )
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .padding(
                        vertical = 16.dp,
                        horizontal = 30.dp
                    ),
                horizontalSpacing = 8.dp,
                verticalSpacing = 16.dp
            ) {
                val menuItems = listOf(
                    "收藏" to MiuixIcons.Useful.Copy,
                    "分享" to MiuixIcons.Useful.Copy,
                    "下载" to MiuixIcons.Useful.Copy,
                    "设置" to MiuixIcons.Useful.Copy,
                    "桌面版" to MiuixIcons.Useful.Copy,
                    "夜间模式" to MiuixIcons.Useful.Copy,
                    "无图模式" to MiuixIcons.Useful.Copy,
                    "全屏" to MiuixIcons.Useful.Copy
                )

                menuItems.forEach { (title, icon) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                isMenuState.value = false
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                modifier = Modifier.size(24.dp),
                                tint = MiuixTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            color = MiuixTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}