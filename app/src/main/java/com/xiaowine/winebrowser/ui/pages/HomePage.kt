package com.xiaowine.winebrowser.ui.pages

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.funny.data_saver.core.rememberDataSaverState
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.R
import com.xiaowine.winebrowser.ui.component.FPSMonitor
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.utils.Utils.showToast
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import java.util.Date

@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL
)
fun TestHomePage() {
    MiuixTheme {
        App()
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
    var booleanExample by rememberDataSaverState(key = "KEY_BOOLEAN_EXAMPLE", initialValue = "false")

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BigTitle()
            Text(
                booleanExample,
                modifier = Modifier.clickable {
                    booleanExample = Date().toString()
                }
            )
            FakeSearchBar(navController)
            Shortcut()
        }
    }

//    Scaffold(
//        floatingToolbarPosition = ToolbarPosition.BottomCenter,
//        content = {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .navigationBarsPadding(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                BigTitle()
//                FakeSearchBar(navController)
//                Shortcut()
//            }
//        },
//        bottomBar = {
//            val items = listOf(
//                NavigationItem("首页", MiuixIcons.Useful.NavigatorSwitch),
//                NavigationItem("设置", MiuixIcons.Useful.Settings)
//            )
//            var selectedItem by remember { mutableIntStateOf(0) }
//            NavigationBar(
//                items = items,
//                selected = selectedItem,
//                onClick = { index ->
//                    selectedItem = index
//                },
//            )
//        }
//    )


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
fun BigTitle() {
    val context: Context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = context.getString(R.string.app_name), fontSize = 50.sp, fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun FakeSearchBar(
    navController: NavController,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .padding(horizontal = 24.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth()
                .height(55.dp)
                .background(MiuixTheme.colorScheme.background)
                .border(
                    width = 2.dp, color = MiuixTheme.colorScheme.onBackground, shape = SmoothRoundedCornerShape(15.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, indication = null
                ) {
                    navController.navigate("search")
                }, contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Search,
                    contentDescription = "Search",
                    tint = MiuixTheme.colorScheme.onBackground,
                )
                Text(
                    text = "搜索或输入网址", color = MiuixTheme.textStyles.main.color, modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Shortcut() {
    val shortcuts = listOf("百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩")
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
        shortcuts.forEach { item ->
            ShortcutItem(
                title = item, onClick = { current.showToast(item) })
        }
    }
}

@Composable
private fun ShortcutItem(
    title: String, onClick: () -> Unit
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
            Icon(
                imageVector = MiuixIcons.Basic.Search,
                contentDescription = title,
                tint = MiuixTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = title, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp)
        )
    }
}
