package com.xiaowine.winebrowser.ui.component.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.ui.viewmodel.HomeViewModel
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
fun HomeShortcut(
    navController: NavController,
    viewModel: HomeViewModel
) {
    var showDialog = remember { mutableStateOf(false) }

    val shortcutLists = viewModel.shortcuts.collectAsState()

    val isLoading by remember { viewModel.isLoading }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(scrollState)
    ) {
        FlowLayout(
            modifier = Modifier.padding(top = 24.dp),
            horizontalSpacing = 8.dp,
            verticalSpacing = 16.dp
        ) {
            if (!isLoading) {
                // 渲染数据库中的快捷方式
                shortcutLists.value.forEach { shortcutEntity ->
                    HomeShortcutItem(
                        itemData = shortcutEntity,
                        onClick = {
                            navController.navigate("browser?url=${shortcutEntity.url}") {
                                launchSingleTop = true
                            }
                        },
                        isEditMode = viewModel.isEditMode,
                        onDelete = { itemData ->
                            viewModel.deleteShortcut(itemData)
                        }
                    )
                }
                HomeShortcutItem(
                    itemData = HomeShortcutEntity(title = "添加", base64Icon = "", url = ""),
                    onClick = {
                        if (!showDialog.value) showDialog.value = true
                    }
                )
            } else {
                repeat(10) {
                    HomeShortcutItemSkeleton()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .size(50.dp)
        )
    }

    AddNewShortcutDialog(
        showDialog = showDialog,
        onSaveShortcut = { title, url ->
            viewModel.addShortcut(title, url)
        }
    )
}

@Composable
fun AddNewShortcutDialog(
    showDialog: MutableState<Boolean>,
    onSaveShortcut: (title: String, url: String) -> Unit
) {
    val titleState = remember { mutableStateOf(TextFieldValue("")) }
    val urlState = remember { mutableStateOf(TextFieldValue("")) }
    val titleError = remember { mutableStateOf(false) }
    val urlError = remember { mutableStateOf(false) }

    SuperDialog(
        title = "添加新快捷方式",
        summary = "请输入网站的名称和URL",
        show = showDialog,
        onDismissRequest = {
            showDialog.value = false
            titleState.value = TextFieldValue("")
            urlState.value = TextFieldValue("")
            titleError.value = false
            urlError.value = false
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 8.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = titleState.value,
                onValueChange = {
                    titleState.value = it
                    titleError.value = it.text.isBlank()
                },
                label = "网站名称",
                singleLine = true,
                borderColor = if (urlError.value) Color.Red else MiuixTheme.colorScheme.primary,
            )

            if (titleError.value) {
                Text(
                    text = "名称不能为空",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = urlState.value,
                onValueChange = {
                    urlState.value = it
                    urlError.value = !isValidUrl(it.text)
                },
                label = "网站URL",
                singleLine = true,
                borderColor = if (urlError.value) Color.Red else MiuixTheme.colorScheme.primary,
            )

            if (urlError.value) {
                Text(
                    text = "请输入有效的URL (以http://或https://开头)",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    text = "取消",
                    onClick = {
                        showDialog.value = false
                        titleState.value = TextFieldValue("")
                        urlState.value = TextFieldValue("")
                    },
                )
                Spacer(modifier = Modifier.size(24.dp))
                TextButton(
                    modifier = Modifier
                        .weight(1f),
                    text = "添加",
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        val title = titleState.value.text.trim()
                        val url = urlState.value.text.trim()

                        titleError.value = title.isBlank()
                        urlError.value = !isValidUrl(url)

                        if (!titleError.value && !urlError.value) {
                            onSaveShortcut(title, url)
                            showDialog.value = false
                            titleState.value = TextFieldValue("")
                            urlState.value = TextFieldValue("")
                        }
                    },
                )
            }
        }
    }
}

fun isValidUrl(url: String): Boolean {
    return url.isNotBlank() && (url.startsWith("http://") || url.startsWith("https://"))
}
