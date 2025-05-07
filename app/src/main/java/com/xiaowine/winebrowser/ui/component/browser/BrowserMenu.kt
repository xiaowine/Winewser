package com.xiaowine.winebrowser.ui.component.browser

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaowine.winebrowser.ui.component.FlowLayout
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Copy
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import kotlin.math.absoluteValue


@Composable
fun BrowserMenu(
    isMenuState: MutableState<Boolean>
) {

    val pagerState = rememberPagerState(pageCount = { 3 })

    BackHandler(
        enabled = isMenuState.value
    ) {
        isMenuState.value = false
    }

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
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = 450.dp)
                    .background(
                        color = MiuixTheme.colorScheme.background,
                        shape = SmoothRoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
                    )
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
                    .padding(
                        vertical = 16.dp,
                        horizontal = 30.dp
                    )
            ) {
                FlowLayout(
                    modifier = Modifier,
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
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 15.dp)
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(pagerState.pageCount) { i ->
                    Box(
                        modifier = Modifier
                            .background(
                                MiuixTheme.colorScheme.primary.copy(
                                    alpha = if (i == pagerState.currentPage)
                                        1f - (pagerState.currentPageOffsetFraction.absoluteValue * 0.8f)
                                    else if ((i == pagerState.currentPage - 1 && pagerState.currentPageOffsetFraction < 0) || (i == pagerState.currentPage + 1 && pagerState.currentPageOffsetFraction > 0))
                                        0.2f + (pagerState.currentPageOffsetFraction.absoluteValue * 0.8f)
                                    else
                                        0.2f
                                )
                            )
                            .size(15.dp, 2.5.dp)
                    )
                }
            }
        }
    }
}
