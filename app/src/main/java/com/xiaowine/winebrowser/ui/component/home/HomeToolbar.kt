package com.xiaowine.winebrowser.ui.component.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaowine.winebrowser.ui.AddIcon
import com.xiaowine.winebrowser.ui.MenuIcon
import top.yukonga.miuix.kmp.basic.FloatingToolbar
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .size(28.dp)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        isMenuState.value = !isMenuState.value
                    },
            ) {
                Icon(
                    modifier = Modifier
                        .fillMaxSize(),
                    imageVector = MenuIcon,
                    contentDescription = "Navigation action",
                    tint = MiuixTheme.colorScheme.onSurface
                )
            }

        }
    }
}
