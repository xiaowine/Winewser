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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Delete
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Preview(showSystemUi = true, device = "id:pixel_9_pro", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchPage() {
    val historyList = listOf("赤霞111111aa珠", "梅洛", "霞多丽", "雷司令", "长相思")
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            SearchField(searchText, focusRequester) {
                searchText = it
            }
            Spacer(modifier = Modifier.height(24.dp))
            HistoryItem(historyList) {
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
        onValueChange = { onValueChange(it) },
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
        maxItemsInEachRow = 5,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        historyList.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MiuixTheme.colorScheme.dividerLine)
                    .clickable {
                        onSelected(item)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item,
                    fontSize = 14.sp,

                    )
            }
        }
    }
}