package com.xiaowine.winebrowser.ui.component.browser

import android.graphics.Canvas
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import kotlin.math.abs
import kotlin.math.roundToInt

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
                    text = "共${listTab.size}个标签页，左右滑动标签可删除",  // 修改提示文本
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
                            DraggableTabPreviewCard(
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

/**
 * 可拖动的标签预览卡片，支持左右滑动删除功能
 */
@Composable
private fun DraggableTabPreviewCard(
    tab: WebViewTabData,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit
) {
    // 设置拖拽相关的状态
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f, Float.VectorConverter) }
    val offsetX = remember { Animatable(0f, Float.VectorConverter) }
    var isDragging by remember { mutableStateOf(false) }

    // 计算水平拖拽进度用于动画效果
    val dragProgress = (offsetX.value / 300f).coerceIn(-1f, 1f)

    // 根据拖拽距离调整卡片的缩放和旋转
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 0.95f - abs(dragProgress) * 0.1f else 1f,
        animationSpec = SpringSpec(stiffness = Spring.StiffnessLow),
        label = "scaleAnimation"
    )

    // 使用16:9的长宽比，更符合一般网页内容的形状
    val aspectRatio = 9f / 16f

    Box(
        modifier = Modifier
            .width(170.dp)
            .aspectRatio(aspectRatio)
            .zIndex(if (isDragging) 1f else 0f) // 添加zIndex以确保拖动时在最顶层显示
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .scale(scale)
            .clip(SmoothRoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.surfaceVariant, SmoothRoundedCornerShape(12.dp))
            // 添加拖拽手势
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        coroutineScope.launch {
                            // 检查是否达到删除阈值（水平拖动超过150px）
                            if (abs(offsetX.value) > 150f) {
                                // 用动画将卡片移出屏幕，然后调用删除回调
                                val targetX = if (offsetX.value > 0) 1000f else -1000f
                                offsetX.animateTo(targetX)
                                onClose()
                            } else {
                                // 未达到删除阈值，弹回原位
                                offsetX.animateTo(0f)
                                offsetY.animateTo(0f)
                            }
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        coroutineScope.launch {
                            // 取消拖拽时弹回原位
                            offsetX.animateTo(0f)
                            offsetY.animateTo(0f)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                    }
                )
            }
            // 点击选择标签（只有在不处于拖拽状态时才响应点击）
            .clickable(enabled = !isDragging) { onSelect() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) MiuixTheme.colorScheme.primary else Color.Transparent,
                shape = SmoothRoundedCornerShape(12.dp)
            )
    ) {
        // 透明度根据拖拽距离变化，拖拽越远越透明
        val alpha = (1f - abs(dragProgress) * 0.6f).coerceIn(0.4f, 1f)

        // 卡片内容区域
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.surfaceVariant.copy(alpha = alpha))
        ) {
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

                // 拖拽提示指示器
                if (abs(dragProgress) > 0.3f) {
                    val deleteColor = if (dragProgress > 0) Color.Red else Color.Red // 可以左滑和右滑都是红色

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(deleteColor.copy(alpha = abs(dragProgress) * 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (abs(dragProgress) > 0.6f) {
                            Text(
                                text = "松开删除",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

//                    // 添加滑动方向指示图标
//                    val arrowOffset = dragProgress * 30f // 箭头偏移量，随拖动距离变化
//
//                    Row(
//                        modifier = Modifier.fillMaxSize(),
//                        horizontalArrangement = if (dragProgress > 0) Arrangement.End else Arrangement.Start,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        if (abs(dragProgress) > 0.4f) {
//                            if (dragProgress < 0) {
//                                Spacer(modifier = Modifier.width(16.dp + arrowOffset.dp))
//                                Icon(
//                                    imageVector = MiuixIcons.Useful.Back,
//                                    contentDescription = "滑动删除",
//                                    tint = Color.White,
//                                    modifier = Modifier.size(24.dp)
//                                )
//                            } else {
//                                Spacer(modifier = Modifier.weight(1f))
//                                Icon(
//                                    imageVector = MiuixIcons.Useful.Back,
//                                    contentDescription = "滑动删除",
//                                    tint = Color.White,
//                                    modifier = Modifier
//                                        .size(24.dp)
//                                        .rotate(180f) // 旋转图标使箭头朝向相反方向
//                                )
//                                Spacer(modifier = Modifier.width(16.dp - arrowOffset.dp))
//                            }
//                        }
//                    }
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
