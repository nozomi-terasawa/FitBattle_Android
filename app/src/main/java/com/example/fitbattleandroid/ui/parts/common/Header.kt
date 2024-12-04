package com.example.fitbattleandroid.ui.parts.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(
    content: @Composable (Modifier) -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val systemUiController = rememberSystemUiController()
    val statusBarColors = primaryContainerDarkMediumContrast

    systemUiController.setStatusBarColor(
        color = statusBarColors,
        darkIcons = true,
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Share Fit",
                        style =
                            MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = onPrimaryDark,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        primaryContainerDarkMediumContrast,
                    ),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding(),
                actions = actions,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content(Modifier.fillMaxSize())
        }
    }
}

// @Composable
// @Preview
// fun HeaderPreview()  {
//    Header(
//        content = {
//            Body {
//                Text("Header")
//            }
//        }
//    )
// }
