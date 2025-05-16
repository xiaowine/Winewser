package com.xiaowine.winebrowser.ui.component.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.theme.AppTheme
import com.xiaowine.winebrowser.utils.Utils.base64ToPainter
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun HomeShortcutItem(
    itemData: HomeShortcutEntity,
    isEditMode: MutableState<Boolean> = remember { mutableStateOf(false) },
    onClick: () -> Unit,
    onDelete: (HomeShortcutEntity) -> Unit = {}
) {
    val rotation = remember { mutableFloatStateOf(0f) }
    val animatedRotation = animateFloatAsState(
        targetValue = if (isEditMode.value) rotation.floatValue else 0f,
        animationSpec = spring(stiffness = 100f)
    )

    LaunchedEffect(isEditMode.value) {
        if (isEditMode.value && itemData.base64Icon != "") {
            while (isEditMode.value) {
                rotation.floatValue = -15f
                delay(150)
                rotation.floatValue = 15f
                delay(150)
            }
        }
    }

    Column(
        modifier = Modifier
            .rotate(animatedRotation.value),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
// 放置图标主体
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(SmoothRoundedCornerShape(12.dp))
                    .background(AppTheme.colorScheme.homeShortBackgroundColor)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { onClick() },
                            onLongPress = {
                                isEditMode.value = true
                            }
                        )
                    },
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

//            删除按钮
            if (isEditMode.value && itemData.base64Icon != "") {
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp, y = (-10).dp)
                        .size(24.dp)
                        .zIndex(1f)
                        .background(
                            color = MiuixTheme.colorScheme.onBackground,
                            shape = SmoothRoundedCornerShape(16.dp)
                        )
                        .clickable { onDelete(itemData) }
                        .align(Alignment.TopEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier
                            .size(16.dp)
                            .rotate(45f),
                        imageVector = AddIcon,
                        tint = MiuixTheme.colorScheme.background,
                        contentDescription = "删除",
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
