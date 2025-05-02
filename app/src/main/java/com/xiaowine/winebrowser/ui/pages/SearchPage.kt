package com.xiaowine.winebrowser.ui.pages

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.AppConfig
import com.xiaowine.winebrowser.AppConfig.isPreview
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableList
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

@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
fun TestSearchPage() {
    MiuixTheme {
        App("search")
    }
}

@Composable
fun SearchPage() {
    // 使用通用 rememberPreviewableList，指定同步逻辑
    val historyList = rememberPreviewableState(
        realData = { AppConfig.searchHistory },
        previewData = listOf("百度", "知乎", "B站"),
        onSync = { AppConfig.searchHistory = it }
    )

    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    SideEffect {
        runCatching {
            focusRequester.requestFocus()
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
            SearchField(searchText, focusRequester) {
                val value = it.text.trim()
                searchText = it
                if (value.isNotEmpty()) {
                    if (!historyList.value.contains(value)) {
                        val currentList = historyList.value.toMutableList()
                        currentList.add(0, value.trim())
                        historyList.value = currentList
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HistoryItem(historyList.value) {
                searchText = TextFieldValue(it)
                focusManager.clearFocus()
            }
        }
    }
}

@Composable
fun SearchField(searchText: TextFieldValue, focusRequester: FocusRequester, onValueChange: (TextFieldValue) -> Unit) {
    TextField(
        value = searchText,
        onValueChange = {
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
        keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
        label = "搜索或输入网址",
        trailingIcon = {
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

@Composable
fun HistoryItem(historyList: List<String>, onSelected: (String) -> Unit) {
    Text("历史记录", style = MiuixTheme.textStyles.main)
    Spacer(modifier = Modifier.height(8.dp))
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
                    .clickable {
                        onSelected(item)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    maxLines = 1,
                    text = if (item.length > 20) item.replace("\n", "").substring(0, 20) + "..." else item,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

