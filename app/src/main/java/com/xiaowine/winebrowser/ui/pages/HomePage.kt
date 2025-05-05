package com.xiaowine.winebrowser.ui.pages

import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.R
import com.xiaowine.winebrowser.config.AppConfig
import com.xiaowine.winebrowser.data.HomeShortcutItemData
import com.xiaowine.winebrowser.data.TestData.shortcuts
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.BrowserMenu
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.ui.theme.AppTheme
import com.xiaowine.winebrowser.utils.ConfigUtils.rememberPreviewableState
import com.xiaowine.winebrowser.utils.Utils.base64ToPainter
import top.yukonga.miuix.kmp.basic.FloatingToolbar
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.ToolbarPosition
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
fun TestHomePage() {
    MiuixTheme {
        App()
    }
}


@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {


    var isMenuState = remember { mutableStateOf(false) }
    val testSate = rememberPreviewableState(
        realData = { AppConfig.title },
        previewData = AppConfig.TITLE_DEFAULT,
        onSync = { AppConfig.title = it }
    )

    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeHeadline()
                Text(
                    testSate.value,
//                    modifier = Modifier.clickable {
////                        testSate.value = Date().toString()
//                    }
                )
                HomeSearchBar(navController)
                HomeShortcut(navController)
            }
        },
        floatingToolbar = {
            HomeToolbar(isMenuState)
        },
        floatingToolbarPosition = ToolbarPosition.BottomEnd
    )
    BrowserMenu(isMenuState)
    AnimatedVisibility(
        visible = BuildConfig.DEBUG
    ) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .captionBarPadding()
                .padding(horizontal = 4.dp)
        )
    }
}


@Composable
fun HomeToolbar(
    isMenuState: MutableState<Boolean>
) {
    FloatingToolbar(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(8.dp),
        cornerRadius = 20.dp
    ) {
        Row {
            val iconsList = listOf(
                AddIcon,
                MenuIcon,
            )
            iconsList.forEachIndexed { i, it ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(28.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            when (i) {
                                0 -> {}
                                1 -> {
                                    isMenuState.value = !isMenuState.value
                                }
                            }
                        },
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize(),
                        imageVector = it,
                        contentDescription = "Navigation action",
                        tint = MiuixTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeadline() {
    val context: Context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = context.getString(R.string.app_name),
            fontSize = 50.sp,
            fontWeight = FontWeight.W600
        )
    }
}

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

@Composable
private fun HomeShortcutItem(
    itemData: HomeShortcutItemData,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(SmoothRoundedCornerShape(12.dp))
                .background(AppTheme.colorScheme.homeShortBackgroundColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            when (itemData.base64Icon) {
                null -> {
                    Text(
                        text = itemData.title.take(1),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MiuixTheme.colorScheme.onBackground
                    )
                }

                "" -> {
                    Icon(
                        imageVector = AddIcon,
                        modifier = Modifier.padding(10.dp),
                        contentDescription = itemData.title,
                    )
                }

                else -> {
                    val painter = base64ToPainter(itemData.base64Icon)
                    Icon(
                        painter = painter,
                        modifier = Modifier.padding(10.dp),
                        contentDescription = itemData.title,
                    )
                }
            }
        }
        Text(
            text = itemData.title,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
fun HomeSearchBar(
    navController: NavController,
    text: String = "搜索或输入网址"
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .padding(horizontal = 24.dp)
                .padding(bottom = 5.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(55.dp)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = 2.dp,
                    color = AppTheme.colorScheme.homeSearchLineColor,
                    shape = SmoothRoundedCornerShape(15.dp)
                )
                .clickable(
                    interactionSource = null,
                    indication = null,
                ) {
                    navController.navigate("browser?url=&isSearch=true")
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Search,
                    contentDescription = "Search",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = text,
                    color = AppTheme.colorScheme.searchTextColor,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}