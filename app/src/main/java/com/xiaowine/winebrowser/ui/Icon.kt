package com.xiaowine.winebrowser.ui


import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private var _arrowLeft: ImageVector? = null
val ArrowLeftIcon: ImageVector
    get() {
        if (_arrowLeft != null) return _arrowLeft!!
        _arrowLeft = ImageVector.Builder(
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
        return _arrowLeft!!
    }

private var _arrowRight: ImageVector? = null
val ArrowRightIcon: ImageVector
    get() {
        if (_arrowRight != null) return _arrowRight!!
        _arrowRight = ImageVector.Builder(
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
        return _arrowRight!!
    }

private var _add: ImageVector? = null
val AddIcon: ImageVector
    get() {
        if (_add != null) return _add!!
        _add = ImageVector.Builder(
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
        return _add!!
    }

private var _menu: ImageVector? = null
val MenuIcon: ImageVector
    get() {
        if (_menu != null) return _menu!!
        _menu = ImageVector.Builder(
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
        return _menu!!
    }