package com.xiaowine.winebrowser.utils

import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import com.xiaowine.winebrowser.database.AppDatabase
import kotlin.math.abs

object Utils {

    fun getDB(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).build()
    }

    fun Context.copyToClipboard(label: String, text: String) {
        val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = android.content.ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }


    /**
     * 将Base64字符串转换为Painter对象
     *
     * @param base64 Base64字符串
     * @return Painter对象
     */
    @Composable
    fun base64ToPainter(base64: String): Painter {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        return BitmapPainter(bitmap.asImageBitmap())
    }

    @Composable
    fun ShowToast(message: String) {
        val context = LocalContext.current
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    fun isColorSimilar(color1: Color, color2: Color, threshold: Float = 0.25f): Boolean {
        val hsv1 = color1.toHsv()
        val hsv2 = color2.toHsv()

        // 处理白色特例（低饱和度且高明度）
        val isColor1White = hsv1[1] < 0.15f && hsv1[2] > 0.9f
        val isColor2White = hsv2[1] < 0.15f && hsv2[2] > 0.9f

        // 处理红色特例（色相接近红色且饱和度高）
        val isColor1Red = (hsv1[0] < 10f || hsv1[0] > 350f) && hsv1[1] > 0.7f
        val isColor2Red = (hsv2[0] < 10f || hsv2[0] > 350f) && hsv2[1] > 0.7f

        // 红色和白色不应该被视为相似
        if ((isColor1Red && isColor2White) || (isColor1White && isColor2Red)) {
            return false
        }

        // 如果两色都是低饱和度（接近灰色系），主要比较明度
        if (hsv1[1] < 0.15f && hsv2[1] < 0.15f) {
            return abs(hsv1[2] - hsv2[2]) <= threshold
        }

        // 计算色相差异（考虑色环）
        val hueDiff = abs(hsv1[0] - hsv2[0]).coerceAtMost(360f - abs(hsv1[0] - hsv2[0])) / 360f

        // 计算饱和度差异，饱和度差异较大时权重更高
        val satDiff = abs(hsv1[1] - hsv2[1]) * 1.5f

        // 计算明度差异
        val valueDiff = abs(hsv1[2] - hsv2[2])

        // 计算总体差异，根据饱和度和明度调整权重
        val totalDiff = (hueDiff * 0.6f + satDiff * 0.8f + valueDiff * 0.6f) / 2f

        return totalDiff <= threshold
    }

    /**
     * 将Color对象转换为HSV数组
     *
     * @return HSV数组，包含色相、饱和度和明度
     */
    fun Color.toHsv(): FloatArray {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(this.toArgb(), hsv)
        return hsv
    }
}
