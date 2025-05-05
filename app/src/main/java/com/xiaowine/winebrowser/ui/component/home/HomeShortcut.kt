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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.data.HomeShortcutItemData
import com.xiaowine.winebrowser.data.TestData.shortcuts
import com.xiaowine.winebrowser.ui.component.FlowLayout


@Composable
fun HomeShortcut(
    navController: NavController
) {
    var scrollState = rememberScrollState()
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
            shortcuts.forEach { shortcutItem ->
                HomeShortcutItem(
                    itemData = shortcutItem,
                    onClick = {
                        navController.navigate("browser?url=${shortcutItem.url}") {
                            launchSingleTop = true
                        }
                    })
            }
            HomeShortcutItem(
                itemData = HomeShortcutItemData("添加", null, ""),
                onClick = {
                    println("aaaaaaaaa")
                })
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .size(50.dp)
        )
    }
}