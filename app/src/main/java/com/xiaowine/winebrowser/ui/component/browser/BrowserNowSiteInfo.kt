package com.xiaowine.winebrowser.ui.component.browser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaowine.winebrowser.ui.theme.AppTheme
import com.xiaowine.winebrowser.utils.Utils.copyToClipboard
import com.xiaowine.winebrowser.utils.Utils.showToast
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.Copy
import top.yukonga.miuix.kmp.icon.icons.useful.Edit
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
fun BrowserNowSiteInfo(
    modifier: Modifier = Modifier,
    urlState: MutableState<String>,
    titleState: MutableState<String>,
    searchText: MutableState<TextFieldValue>
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
        ) {
            Column(
                modifier = Modifier.weight(0.7f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    maxLines = 1,
                    text = titleState.value,
                    fontSize = 16.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = MiuixTheme.colorScheme.onBackground.copy(0.7f)
                )
                Text(
                    maxLines = 1,
                    text = urlState.value,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    color = MiuixTheme.colorScheme.onBackground.copy(0.7f)
                )
            }
            Row(
                modifier = Modifier.weight(0.3f),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        context.copyToClipboard("URL", urlState.value)
                        context.showToast("已复制链接")
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = MiuixIcons.Useful.Copy,
                        contentDescription = "复制",
                        tint = AppTheme.colorScheme.iconTintColor.copy(0.7f)
                    )
                    Text(
                        text = "复制",
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onBackground.copy(0.7f)
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable(
                            indication = null,
                            interactionSource = null
                        ) {
                            searchText.value = TextFieldValue(urlState.value)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = MiuixIcons.Useful.Edit,
                        contentDescription = "编辑",
                        tint = AppTheme.colorScheme.iconTintColor.copy(0.7f)
                    )
                    Text(
                        text = "编辑",
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onBackground.copy(0.7f)
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

