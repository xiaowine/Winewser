package com.xiaowine.winebrowser.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.xiaowine.winebrowser.config.AppConfig

object ConfigUtils {

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