package com.xiaowine.winebrowser.ui.component.home

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.data.HomeShortcutItemData
import com.xiaowine.winebrowser.data.InitData.defaultShortcuts
import com.xiaowine.winebrowser.data.entity.AppEntity
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.utils.Utils.getDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@Composable
fun HomeShortcut(
    navController: NavController,
) {
    var shortcutList = remember { mutableStateOf<List<HomeShortcutEntity>>(emptyList()) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        initializeIfNeededAndGet(context, shortcutList)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(scrollState)
    ) {
        FlowLayout(
            modifier = Modifier,
            horizontalSpacing = 8.dp,
            verticalSpacing = 16.dp
        ) {
            if (shortcutList.value.isNotEmpty()) {
                // 渲染数据库中的快捷方式
                shortcutList.value.forEach { shortcutEntity ->
                    HomeShortcutItem(
                        itemData = HomeShortcutItemData(
                            title = shortcutEntity.title,
                            base64Icon = shortcutEntity.base64Icon,
                            url = shortcutEntity.url
                        ),
                        onClick = {
                            navController.navigate("browser?url=${shortcutEntity.url}") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                HomeShortcutItem(
                    itemData = HomeShortcutItemData("添加", null, ""),
                    onClick = {
                        println("aaaaaaaaa")
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