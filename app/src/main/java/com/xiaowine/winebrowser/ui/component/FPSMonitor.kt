package com.xiaowine.winebrowser.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Text
import kotlin.math.roundToInt

@Composable
fun FPSMonitor(modifier: Modifier = Modifier) {
    var fps by remember { mutableIntStateOf(0) }
    var lastTime by remember { mutableLongStateOf(System.nanoTime()) }
    var frameCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            fps = frameCount
            frameCount = 0
        }
    }

    // Count frames
    val currentTime = System.nanoTime()
    lastTime = currentTime
    frameCount++

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0x88000000))
            .alpha(0.7f)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$fps FPS",
            color = if (fps >= 55) Color.Green else if (fps >= 30) Color.Yellow else Color.Red,
            fontSize = 12.sp
        )
    }
}
