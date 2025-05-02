package com.xiaowine.winebrowser.ui.pages

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.R
import com.xiaowine.winebrowser.ui.FPSMonitor
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
@Preview(showSystemUi = true, device = "spec:parent=pixel_fold")
@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL)
fun TestHomePage() {
    MiuixTheme {
        App()
    }
}

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            BigTitle()
            FakeSearchBar(navController)
            Shortcut(Modifier.weight(1f))
        }
    }
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
            text = context.getString(R.string.app_name),
            fontSize = 50.sp,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun FakeSearchBar(
    navController: NavController,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
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
                    width = 2.dp,
                    color = MiuixTheme.colorScheme.onBackground,
                    shape = SmoothRoundedCornerShape(15.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    navController.navigate("search")
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
                    text = "搜索或输入网址",
                    color = MiuixTheme.textStyles.main.color,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun Shortcut(modifier: Modifier = Modifier) {
    val shortcuts = listOf("百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩", "百度", "哔哩哔哩", "知乎", "GitHub", "微博", "微信", "QQ", "淘宝", "京东", "小红书", "百度", "哔哩哔哩")
    val current = LocalContext.current
    val density = LocalDensity.current
    val dp400 = 440.dp
    // 图标宽度(56dp) + 水平padding(12dp*2)
    val itemWidth = 80.dp

    // 水平边距
    val horizontalPadding = 24.dp

    // 获取窗口宽度（以dp为单位）
    val windowWidthPx = getWindowSize().width
    val windowWidthDp = with(density) { windowWidthPx.toDp() }.let {
        if (it > dp400) dp400 else it
    }

    // 考虑水平边距
    val availableWidthDp = windowWidthDp - horizontalPadding * 2

    // 计算每行最多能放多少个项目
    val itemsPerRow = (availableWidthDp / itemWidth).toInt().coerceAtLeast(1)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(itemsPerRow),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .widthIn(max = dp400)
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            items(shortcuts) { shortcut ->
                ShortcutItem(shortcut) {

                }
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    title: String,
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
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = MiuixIcons.Basic.Search,
                contentDescription = title,
                tint = MiuixTheme.colorScheme.onBackground,
            )
        }
        Text(
            text = title,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
