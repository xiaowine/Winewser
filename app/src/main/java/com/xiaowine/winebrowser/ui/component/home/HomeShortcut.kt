package com.xiaowine.winebrowser.ui.component.home

import android.content.Context
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.data.InitData.defaultShortcuts
import com.xiaowine.winebrowser.data.entity.AppEntity
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.utils.Utils.getDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeShortcut(
    navController: NavController,
    isEditMode: MutableState<Boolean>,
) {
    var showDialog = remember { mutableStateOf(false) }
    var shortcutList = remember { mutableStateOf<List<HomeShortcutEntity>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        initializeIfNeededAndGet(context, shortcutList)
    }

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
            if (shortcutList.value.isNotEmpty()) {
                // 渲染数据库中的快捷方式
                shortcutList.value.forEach { shortcutEntity ->
                    HomeShortcutItem(
                        itemData = shortcutEntity,
                        onClick = {
                            navController.navigate("browser?url=${shortcutEntity.url}") {
                                launchSingleTop = true
                            }
                        },
                        isEditMode = isEditMode,
                        onDelete = { itemData ->
                            coroutineScope.launch(Dispatchers.IO) {
                                deleteShortcut(context, itemData)
                                val db = getDB(context)
                                shortcutList.value = db.homeShortcutData().getAllDirect()
                            }
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
            coroutineScope.launch {
                saveNewShortcut(context, title, url)
                shortcutList.value = withContext(Dispatchers.IO) {
                    getDB(context).homeShortcutData().getAllDirect()
                }
            }
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
    val context = LocalContext.current

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

suspend fun saveNewShortcut(context: Context, title: String, url: String) {
    withContext(Dispatchers.IO) {
        val db = getDB(context)
        val newShortcut = HomeShortcutEntity(
            title = title,
            url = url,
            base64Icon = null // 可以在这里添加默认图标或者留空
        )
        db.homeShortcutData().insert(newShortcut)
    }
}

suspend fun initializeIfNeededAndGet(context: Context, shortcutList: MutableState<List<HomeShortcutEntity>>) {
    withContext(Dispatchers.IO) {
        val db = getDB(context)
        val appEntity = db.appData().get()

        // 检查是否需要初始化
        if (appEntity == null || !appEntity.isInitialized) {
            db.runInTransaction {
                // 添加默认快捷方式
                db.homeShortcutData().insertAll(defaultShortcuts)
            }
            // 更新或创建应用配置
            if (appEntity == null) {
                db.appData().insert(AppEntity(isInitialized = true))
            } else {
                db.appData().update(appEntity.copy(isInitialized = true))
            }
        }

        // 无论是否初始化，都加载最新的快捷方式列表
        shortcutList.value = db.homeShortcutData().getAllDirect()
    }
}

suspend fun deleteShortcut(context: Context, shortcut: HomeShortcutEntity) {
    withContext(Dispatchers.IO) {
        val db = getDB(context)
        db.homeShortcutData().delete(shortcut)
    }
}
