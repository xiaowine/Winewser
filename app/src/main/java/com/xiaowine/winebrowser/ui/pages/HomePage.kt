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
import com.xiaowine.winebrowser.App
import com.xiaowine.winebrowser.BuildConfig
import com.xiaowine.winebrowser.R
import com.xiaowine.winebrowser.ui.FPSMonitor
import com.xiaowine.winebrowser.ui.component.FlowLayout
import com.xiaowine.winebrowser.utils.Utils.showToast
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

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

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
) {
    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BigTitle()
            FakeSearchBar(navController)
            Shortcut()
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
    val shortcuts = listOf("aaaaa", "bbbbbb", "ccccc", "ccccc", "bbbbbb", "ccccc", "bbbbbb", "ccccc")
    val current = LocalContext.current

    FlowLayout(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .widthIn(max = 400.dp),
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
