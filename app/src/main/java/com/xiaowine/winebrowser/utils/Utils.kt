package com.xiaowine.winebrowser.utils

import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import com.xiaowine.winebrowser.config.AppConfig

object Utils {

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

    /**
     * 在预览模式下返回示例数据，在真实环境中返回真实数据
     *
     * @param realData 实际环境中的数据
     * @param previewData 预览模式下的示例数据
     * @param onSync 非预览模式下，数据变更时的同步回调（如保存到配置/数据库等）
     * @return 可变状态列表，包含根据当前模式选择的数据
     */
    @Composable
    inline fun <reified T> rememberPreviewableList(
        crossinline realData: () -> List<T>,
        previewData: List<T>,
        noinline onSync: ((List<T>) -> Unit)? = null
    ): MutableList<T> {
        val isPreview = AppConfig.isPreview
        val stateList = remember {
            mutableStateListOf(*if (!isPreview) realData().toTypedArray() else previewData.toTypedArray())
        }
        // 仅在非预览模式下，操作时同步到 onSync
        return object : MutableList<T> by stateList {
            private fun sync() {
                if (!isPreview) {
                    onSync?.invoke(ArrayList(stateList))
                }
            }

            override fun add(element: T): Boolean {
                val result = stateList.add(element)
                sync()
                return result
            }

            override fun add(index: Int, element: T) {
                stateList.add(index, element)
                sync()
            }

            override fun addAll(elements: Collection<T>): Boolean {
                val result = stateList.addAll(elements)
                sync()
                return result
            }

            override fun addAll(index: Int, elements: Collection<T>): Boolean {
                val result = stateList.addAll(index, elements)
                sync()
                return result
            }

            override fun remove(element: T): Boolean {
                val result = stateList.remove(element)
                sync()
                return result
            }

            override fun removeAt(index: Int): T {
                val result = stateList.removeAt(index)
                sync()
                return result
            }

            override fun removeAll(elements: Collection<T>): Boolean {
                val result = stateList.removeAll(elements)
                sync()
                return result
            }

            override fun clear() {
                stateList.clear()
                sync()
            }
        }
    }

    /**
     * 预览/真实模式下的通用状态管理（单值）
     */
    @Composable
    fun <T> rememberPreviewableState(
        realData: () -> T,
        previewData: T,
        onSync: ((T) -> Unit)? = null
    ): MutableState<T> {
        val isPreview = AppConfig.isPreview
        val state = remember {
            mutableStateOf(if (!isPreview) realData() else previewData)
        }
        return remember {
            object : MutableState<T> by state {
                override var value: T
                    get() = state.value
                    set(value) {
                        state.value = value
                        if (!isPreview) {
                            onSync?.invoke(value)
                        }
                    }
            }
        }
    }
}
