package com.xiaowine.winebrowser.ui.component.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xiaowine.winebrowser.ui.theme.AppTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun HomeShortcutItemSkeleton() {
    // 创建无限重复的动画
    val transition = rememberInfiniteTransition("skeleton_animation")
    
    // 使用动画值控制shimmer效果的位置
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    // 创建渐变画笔用于shimmer效果
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.4f),
        Color.LightGray.copy(alpha = 0.7f),
        Color.LightGray.copy(alpha = 0.4f)
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 200f, 0f),
        end = Offset(translateAnim.value + 200f, 0f)
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 骨架图标区域
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(SmoothRoundedCornerShape(12.dp))
                .background(brush)
        )
        
        // 骨架文本区域
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(48.dp)
                .height(14.dp)
                .clip(SmoothRoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}

/**
 * 多个骨架项的横向组合
 */
@Composable
fun HomeShortcutSkeletonRow(itemCount: Int = 4) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        repeat(itemCount) {
            HomeShortcutItemSkeleton()
            if (it < itemCount - 1) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}
