package com.xiaowine.winebrowser.ui.component.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.data.HomeShortcutItemData
import com.xiaowine.winebrowser.data.entity.HomeShortcutEntity
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.utils.Utils.getDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext


@Composable
fun HomeShortcut(
    navController: NavController,
) {
    var shortcutList by remember { mutableStateOf<List<HomeShortcutEntity>>(emptyList()) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val db = getDB(context)

        withContext(Dispatchers.IO) {
            shortcutList = db.homeShortcutData().getAllDirect()
            if (shortcutList.isEmpty()) {
                db.homeShortcutData().getAll().collectLatest {
                    if (it.isNotEmpty()) {
                        shortcutList = it
                    }
                }
            }
        }
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
            if (shortcutList.isNotEmpty()) {
                // 渲染数据库中的快捷方式
                shortcutList.forEach { shortcutEntity ->
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
            }
            HomeShortcutItem(
                itemData = HomeShortcutItemData("添加", null, ""),
                onClick = {
                    println("aaaaaaaaa")
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .size(50.dp)
        )
    }
}
