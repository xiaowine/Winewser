package com.xiaowine.winebrowser.ui.component.browser

import android.graphics.Bitmap
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.xiaowine.winebrowser.ui.theme.AppTheme
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.NavigatorSwitch
import top.yukonga.miuix.kmp.icon.icons.useful.Refresh
import top.yukonga.miuix.kmp.icon.icons.useful.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun BrowserSearchField(
    modifier: Modifier = Modifier,
    searchText: TextFieldValue,
    focusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit,
    onSearch: () -> Unit,
    webViewState: MutableState<WebView?>,
    isSearchState: MutableState<Boolean>,
    siteIconState: MutableState<Bitmap?>,
    isLoading: () -> Boolean,
) {
    TextField(
        value = searchText,
        onValueChange = {
            val filteredText = it.copy(text = it.text.replace("\n", ""))
            onValueChange(filteredText)
        },
        useLabelAsPlaceholder = true,
        cornerRadius = 15.dp,
        backgroundColor = MiuixTheme.colorScheme.background,
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .border(
                width = 2.dp,
                color = AppTheme.colorScheme.homeSearchLineColor,
                shape = SmoothRoundedCornerShape(15.dp)
            )
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            if (searchText.text.trim().isNotEmpty()) {
                onSearch()
            }
        }),
        label = "搜索或输入网址",
        leadingIcon = {
            val modifier = Modifier
                .padding(horizontal = 10.dp)
                .size(24.dp)
            if (!isSearchState.value) {
                if (isLoading()) {
                    InfiniteProgressIndicator(
                        modifier = modifier
                    )
                } else {
                    if (siteIconState.value != null) {
                        Image(
                            modifier = modifier,
                            painter = BitmapPainter(siteIconState.value!!.asImageBitmap()),
                            contentDescription = null,
                        )
                    } else {
                        Icon(
                            modifier = modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = null
                                ) {},
                            imageVector = MiuixIcons.Useful.NavigatorSwitch,
                            contentDescription = "Search",
                            tint = AppTheme.colorScheme.iconTintColor
                        )
                    }
                }
            } else {
                Spacer(
                    modifier = Modifier
                        .size(16.dp)
                )
            }
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clickable(
                        indication = null,
                        interactionSource = null
                    ) {
                        if (isSearchState.value) {
                            onSearch()
                        } else {
                            webViewState.value!!.reload()
                        }
                    },
                imageVector = if (isSearchState.value) MiuixIcons.Useful.Search else MiuixIcons.Useful.Refresh,
                contentDescription = "Search",
                tint = if (searchText.text.trim().isEmpty()) {
                    AppTheme.colorScheme.iconTintColor.copy(alpha = 0.3f)
                } else {
                    AppTheme.colorScheme.iconTintColor
                }
            )
        }
    )
}
