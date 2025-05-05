@file:Suppress("ObjectPropertyName")

package com.xiaowine.winebrowser.ui


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowLeftIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "ArrowLeft",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.EvenOdd
        ) {
            // 再增大一点箭头尺寸
            moveTo(15.0f, 6.0f)
            lineTo(9.0f, 12.0f)
            lineTo(15.0f, 18.0f)
        }
    }.build()
}

val ArrowRightIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "ArrowRight",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(9.0f, 6.0f)
            lineTo(15.0f, 12.0f)
            lineTo(9.0f, 18.0f)
        }
    }.build()
}

val AddIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Add",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = null, // 将fill设置为null
            stroke = SolidColor(Color.Black), // 添加stroke参数
            strokeLineWidth = 2.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(12.0f, 4.5f)
            lineTo(12.0f, 19.5f)
            moveTo(4.5f, 12.0f)
            lineTo(19.5f, 12.0f)
        }
    }.build()
}

val MenuIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Menu",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = null, // 将fill设置为null
            stroke = SolidColor(Color.Black), // 添加stroke参数
            strokeLineWidth = 2.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.EvenOdd
        ) {
            moveTo(4.8f, 6.0f)
            lineTo(19.2f, 6.0f)
            moveTo(4.8f, 12.0f)
            lineTo(19.2f, 12.0f)
            moveTo(4.8f, 18.0f)
            lineTo(19.2f, 18.0f)
        }
    }.build()
}

val LinkIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Link45",
        defaultWidth = 24.0.dp,
        defaultHeight = 24.0.dp,
        viewportWidth = 24.0f,
        viewportHeight = 24.0f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.0f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathFillType = PathFillType.EvenOdd
        ) {
            // 创建一个45度倾斜的链接图标
            // 第一个环 - 左下角
            moveTo(8.0f, 14.0f)
            arcTo(4.0f, 4.0f, 0.0f, false, false, 10.0f, 16.0f)
            lineTo(13.0f, 16.0f)
            arcTo(4.0f, 4.0f, 0.0f, true, false, 16.0f, 10.0f)
            lineTo(13.0f, 10.0f)
            arcTo(4.0f, 4.0f, 0.0f, false, false, 11.0f, 12.0f)

            // 第二个环 - 右上角
            moveTo(16.0f, 10.0f)
            arcTo(4.0f, 4.0f, 0.0f, false, false, 14.0f, 8.0f)
            lineTo(11.0f, 8.0f)
            arcTo(4.0f, 4.0f, 0.0f, true, false, 8.0f, 14.0f)
            lineTo(11.0f, 14.0f)
            arcTo(4.0f, 4.0f, 0.0f, false, false, 13.0f, 12.0f)

            // 中间的连接线
            moveTo(10.0f, 10.0f)
            lineTo(14.0f, 14.0f)
        }
    }.build()
}