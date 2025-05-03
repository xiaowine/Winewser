package com.xiaowine.winebrowser.ui.pages

import android.net.Uri
import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Delete
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

/**
 * 搜索页面预览函数
 */
@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
fun TestSearchPage() {
    MiuixTheme {
        App(startDestination = "search", navController = rememberNavController())
    }
}

/**
 * 搜索页面 Composable
 *
 * @param navController 导航控制器，用于页面跳转
 */
@Composable
fun SearchPage(navController: NavController) {
    // 搜索历史记录状态，支持预览
    val historyList = rememberPreviewableState(
        realData = { AppConfig.searchHistory },
        previewData = listOf("百度", "知乎", "B站"),
        onSync = { AppConfig.searchHistory = it } // 同步更新到 AppConfig
    )

    // 搜索框文本状态
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    // 焦点管理
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    SideEffect {
        runCatching {
            focusRequester.requestFocus()
        }
    }

    // 执行搜索或导航的 Lambda 函数
    val performSearchOrNavigate: (String) -> Unit = { query ->
        val trimmedQuery = query.trim() // 去除首尾空格
        if (trimmedQuery.isNotEmpty()) {
            // 如果历史记录中不包含该查询，则添加到历史记录列表的开头
            if (!historyList.value.contains(trimmedQuery)) {
                val currentList = historyList.value.toMutableList()
                currentList.add(0, trimmedQuery)
                historyList.value = currentList
            }

            // 判断输入是 URL 还是搜索词
            val url = if (Patterns.WEB_URL.matcher(trimmedQuery).matches() || trimmedQuery.contains("://")) {
                // 如果是 URL 但不包含协议头，则添加 https://
                if (!trimmedQuery.contains("://")) "https://$trimmedQuery" else trimmedQuery
            } else {
                // 如果是搜索词，则使用 Bing 搜索引擎进行搜索
                "https://www.bing.com/search?q=${Uri.encode(trimmedQuery)}"
            }
            // 清除焦点，隐藏键盘
            focusManager.clearFocus()
            // 导航到主页并加载 URL
            navController.navigate("home?url=${Uri.encode(url)}") {
                // TODO: 可在此处添加导航选项
            }
        }
    }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(top = 6.dp)
                .padding(16.dp)
        ) {
            // 搜索输入框
            SearchField(
                searchText = searchText,
                focusRequester = focusRequester,
                onValueChange = { searchText = it }, // 更新搜索文本状态
                onSearch = { performSearchOrNavigate(searchText.text) } // 执行搜索
            )
            Spacer(modifier = Modifier.height(8.dp))
            // 历史记录项
            HistoryItem(
                historyList = historyList.value,
                onSelected = { performSearchOrNavigate(it) } // 点击历史记录项执行搜索
            )
        }
    }
}

/**
 * 搜索输入框 Composable
 *
 * @param searchText 当前搜索文本状态
 * @param focusRequester 焦点请求器
 * @param onValueChange 文本变化时的回调函数
 * @param onSearch 点击键盘搜索按钮时的回调函数
 */
@Composable
fun SearchField(
    searchText: TextFieldValue,
    focusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = {
            // 过滤换行符
            val filteredText = it.copy(text = it.text.replace("\n", ""))
            onValueChange(filteredText)
        },
        useLabelAsPlaceholder = true,
        cornerRadius = 15.dp,
        backgroundColor = MiuixTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .border(
                width = 2.dp,
                color = MiuixTheme.colorScheme.onBackground,
                shape = SmoothRoundedCornerShape(15.dp)
            )
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), // 设置键盘动作为搜索
        keyboardActions = KeyboardActions(onSearch = { // 处理键盘搜索动作
            onSearch()
        }),
        label = "搜索或输入网址", // 标签文本
        trailingIcon = {
            // 当输入框不为空时显示清除按钮
            if (searchText.text.isNotEmpty()) {
                IconButton(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = { onValueChange(TextFieldValue("")) }
                ) {
                    Icon(
                        imageVector = MiuixIcons.Useful.Delete,
                        contentDescription = "清除"
                    )
                }
            }
        }
    )
}

/**
 * 历史记录项 Composable
 *
 * @param historyList 历史记录列表
 * @param onSelected 选中历史记录项时的回调函数
 */
@Composable
fun HistoryItem(historyList: List<String>, onSelected: (String) -> Unit) {
    Text("历史记录", style = MiuixTheme.textStyles.main)
    Spacer(modifier = Modifier.height(8.dp))
    // 流式布局，用于显示历史记录标签
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        historyList.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(SmoothRoundedCornerShape(12.dp))
                    .background(MiuixTheme.colorScheme.dividerLine)
                    .clickable { onSelected(item) } // 添加点击事件
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // 过滤换行符
                val cleanItem = item.replace("\n", "")
                Text(
                    maxLines = 1, // 最多显示一行
                    // 限制最大长度，超出部分显示省略号
                    text = if (cleanItem.length > 20) "${cleanItem.take(20)}..." else cleanItem,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

