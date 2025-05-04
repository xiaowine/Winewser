package com.xiaowine.winebrowser.ui.pages

import android.annotation.SuppressLint
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
import com.xiaowine.winebrowser.config.AppConfig
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.R
import com.xiaowine.winebrowser.data.HomeShortcutItemData
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import com.xiaowine.winebrowser.ui.component.BrowserMenu
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.utils.Utils.base64ToPainter
import com.xiaowine.winebrowser.utils.Utils.rememberPreviewableState
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

@SuppressLint("AutoboxingStateCreation", "SetJavaScriptEnabled")
@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
//    var isInHtmlState by remember { mutableStateOf(urlToLoad != null) }

    val context = LocalContext.current

    var isMenuState = remember { mutableStateOf(false) }
    val testSate = rememberPreviewableState(
        realData = { AppConfig.title },
        previewData = "aaa",
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
                HomeBigTitle()
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
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun HomeBigTitle() {
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
    val shortcuts = listOf(
        HomeShortcutItemData(
            "知乎", "https://www.zhihu.com", "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"
        ),
        HomeShortcutItemData(
            "百度", "https://www.baidu.com", "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"
        ),
        HomeShortcutItemData("微博", "https://weibo.com"),
        HomeShortcutItemData("QQ", "https://im.qq.com"),
        HomeShortcutItemData("微信", "https://weixin.qq.com"),
        HomeShortcutItemData("抖音", "https://www.douyin.com"),
        HomeShortcutItemData("快手", "https://www.kuaishou.com"),
        HomeShortcutItemData("淘宝", "https://www.taobao.com"),
        HomeShortcutItemData(
            "京东", "https://www.jd.com", "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"
        ),
    )
    val current = LocalContext.current

    var scrollState = rememberScrollState()
    FlowLayout(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .widthIn(max = 400.dp)
            .verticalScroll(scrollState),
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
                .background(MiuixTheme.colorScheme.dividerLine)
                .clickable(onClick = onClick), contentAlignment = Alignment.Center
        ) {
            if (itemData.icon != null) {
                val painter = base64ToPainter(itemData.icon)
                Icon(
                    painter = painter,
                    modifier = Modifier.padding(10.dp),
                    contentDescription = itemData.title,
                )
            } else {
                Text(
                    text = itemData.title.take(1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.onBackground
                )
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
                    color = MiuixTheme.colorScheme.onBackground,
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
                    color = MiuixTheme.textStyles.main.color,
                    modifier = Modifier.padding(start = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis // 超出部分显示省略号
                )
            }
        }
    }
}