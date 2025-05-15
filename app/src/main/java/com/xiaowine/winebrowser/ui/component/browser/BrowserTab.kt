package com.xiaowine.winebrowser.ui.component.browser

import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.core.view.isVisible
import com.xiaowine.winebrowser.data.WebViewTabData
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.component.FlowLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Back
import top.yukonga.miuix.kmp.icon.icons.useful.Cut
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun BrowserTab(
    modifier: Modifier = Modifier,
    show: MutableState<Boolean>,
    listTab: SnapshotStateList<WebViewTabData>,
    onSelection: (WebViewTabData) -> Unit,
    onClose: (WebViewTabData) -> Unit = {},
    onCreateNew: () -> Unit = {},
    currentTabIndex: Int = 0
) {
    val coroutineScope = rememberCoroutineScope()

    // 当标签菜单显示时，为所有缺少预览的标签页生成预览
    LaunchedEffect(show.value) {
        if (show.value) {
            // 使用协程处理可能耗时的截图操作
            coroutineScope.launch {
                withContext(Dispatchers.Main) {
                    // 优先为当前标签页生成预览
                    val currentTab = if (currentTabIndex >= 0 && currentTabIndex < listTab.size) {
                        listTab[currentTabIndex]
                    } else null

                    // 先为当前标签页生成预览
                    currentTab?.let { tab ->
                        captureTabPreview(tab)
                    }

                    // 然后异步为其他所有标签页生成预览
                    listTab.forEachIndexed { index, tab ->
                        if (index != currentTabIndex && tab.thumbnail == null && tab.webView != null) {
                            captureTabPreview(tab)
                        }
                    }
                }
            }
        }
    }

    // 根据show状态决定是否显示对话框
    if (show.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable(
                    indication = null,
                    interactionSource = null
                ) {
                    show.value = false
                }
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .statusBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 页面头部
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // 标题
                    Text(
                        text = "标签页管理",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // 关闭按钮
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { show.value = false }
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Useful.Cut,
                            contentDescription = "关闭",
                            tint = Color.White
                        )
                    }
                }

                // 标签数量提示
                Text(
                    text = "共${listTab.size}个标签页",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 标签页列表
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FlowLayout(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        // 显示所有标签页
                        listTab.forEachIndexed { index, tab ->
                            TabPreviewCard(
                                tab = tab,
                                isSelected = index == currentTabIndex,
                                onSelect = {
                                    onSelection(tab)
                                    show.value = false
                                },
                                onClose = { onClose(tab) }
                            )
                        }

                        // 新建标签页按钮放在前面
                        NewTabButton(onClick = {
                            onCreateNew()
                            show.value = false
                        })
                    }
                }
            }
        }
    }
}

/**
 * 为标签页生成预览图
 */
private suspend fun captureTabPreview(tab: WebViewTabData) {
    val webView = tab.webView ?: return

    // 确保WebView有内容
    if (webView.width <= 0 || webView.height <= 0) return

    withContext(Dispatchers.Main) {
        try {
            // 记录当前可见状态
            val wasVisible = webView.isVisible
            if (!wasVisible) {
                webView.visibility = android.view.View.VISIBLE
            }


            // 创建适当大小的缩略图
            val scale = 0.5f // 缩小比例以提高性能
            val width = (webView.width * scale).toInt().coerceAtLeast(1)
            val height = (webView.height * scale).toInt().coerceAtLeast(1)

            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            canvas.scale(scale, scale)

            // 绘制WebView内容到缩略图
            webView.draw(canvas)
            tab.thumbnail = bitmap

            // 还原之前的可见状态
            if (!wasVisible) {
                webView.visibility = android.view.View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
private fun TabPreviewCard(
    tab: WebViewTabData,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    // 使用16:9的长宽比，更符合一般网页内容的形状
    val aspectRatio = 9f / 16f

    Box(
        modifier = Modifier
            .width(170.dp)
            .aspectRatio(aspectRatio)
            .clip(SmoothRoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.surfaceVariant, SmoothRoundedCornerShape(12.dp))
            .clickable(
                indication = null,
                interactionSource = null
            ) { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MiuixTheme.colorScheme.primary else Color.Transparent,
                shape = SmoothRoundedCornerShape(12.dp)
            )
    ) {
        // 标签内容区域
        Column {
            // 缩略图区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // 使用标签页中的缩略图（实时捕获的）
                if (tab.thumbnail != null) {
                    Image(
                        bitmap = tab.thumbnail!!.asImageBitmap(),
                        contentDescription = "页面预览",
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // 没有缩略图时显示占位色块，使用网站主题色
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(tab.themeColor).copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        // 显示网站首字母或图标
                        tab.icon?.let { icon ->
                            Image(
                                bitmap = icon.asImageBitmap(),
                                contentDescription = "网站图标",
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(SmoothRoundedCornerShape(16.dp))
                            )
                        } ?: run {
                            // 显示网站名首字母
                            val displayChar = when {
                                tab.title.isNotEmpty() -> tab.title.first().toString()
                                tab.url.isNotEmpty() -> {
                                    val domain = tab.url.replace("https://", "")
                                        .replace("http://", "")
                                        .split("/").firstOrNull() ?: ""
                                    if (domain.isNotEmpty()) domain.first().toString() else "N"
                                }

                                else -> "N"
                            }

                            Text(
                                text = displayChar,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(tab.themeColor),
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(
                                        Color.White.copy(alpha = 0.7f),
                                        SmoothRoundedCornerShape(16.dp)
                                    )
                                    .padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // 右上角关闭按钮
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(26.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Back,
                        contentDescription = "关闭标签页",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // 当前活动标签标识
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(8.dp)
                            .background(MiuixTheme.colorScheme.primary, CircleShape)
                    )
                }
            }

            // 标签页信息栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MiuixTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 网站图标
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            if (tab.icon == null) Color(tab.themeColor).copy(alpha = 0.2f) else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    tab.icon?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "网站图标",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 标题
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),  // 使用weight让文本填充剩余空间
                    text = if (tab.title.isNotEmpty()) tab.title else "新标签页",
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MiuixTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun NewTabButton(onClick: () -> Unit) {
    val aspectRatio = 9f / 16f

    Box(
        modifier = Modifier
            .width(170.dp)
            .aspectRatio(aspectRatio)
            .shadow(2.dp, SmoothRoundedCornerShape(12.dp))
            .clip(SmoothRoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.background.copy(alpha = 0.7f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // 加号图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AddIcon,
                    contentDescription = "新建标签页",
                    modifier = Modifier.size(32.dp),
                    tint = MiuixTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 文字说明
            Text(
                text = "新建标签页",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MiuixTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
