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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
@Preview(showSystemUi = true, device = "id:pixel_9_pro")
fun TestHomePage() {
    MiuixTheme {
        App()
    }
}

@Composable
@Preview(showSystemUi = true, device = "id:pixel_9_pro", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES or android.content.res.Configuration.UI_MODE_TYPE_NORMAL)
fun TestDarkHomePage() {
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
    Box(
        modifier = Modifier
            .padding(top = 50.dp, start = 24.dp, end = 24.dp)
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
