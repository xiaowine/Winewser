package com.xiaowine.winebrowser.ui.component.browser

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaowine.winebrowser.data.entity.SearchHistoryEntity
import com.xiaowine.winebrowser.ui.LinkIcon
import com.xiaowine.winebrowser.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun BrowserSearchHistoryPanel(
    modifier: Modifier = Modifier,
    historyList: List<SearchHistoryEntity>,
    onSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        Text("历史记录", style = MiuixTheme.textStyles.main)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            historyList.forEach { item ->
                val cleanItem = item.content.replace("\n", "")
                val isLink = Patterns.WEB_URL.matcher(cleanItem).matches() || cleanItem.contains("://")
                Row(
                    modifier = Modifier
                        .clip(SmoothRoundedCornerShape(12.dp))
                        .background(AppTheme.colorScheme.searchHistoryBackgroundColor)
                        .clickable { onSelected(item.content) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isLink) {
                        Icon(
                            modifier = Modifier
                                .rotate(90f)
                                .size(20.dp),
                            imageVector = LinkIcon,
                            contentDescription = "链接",
                            tint = AppTheme.colorScheme.iconTintColor
                        )
                    }
                    Text(
                        maxLines = 1,
                        text = cleanItem,
                        fontSize = 14.sp,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
