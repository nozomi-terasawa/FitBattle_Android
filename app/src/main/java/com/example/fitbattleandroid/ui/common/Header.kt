package com.example.fitbattleandroid.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun Header(content: @Composable () -> Unit) {

    val systemUiController = rememberSystemUiController()
    val statusBarColors = primaryContainerDarkMediumContrast

    systemUiController.setStatusBarColor(
        color = statusBarColors,
        darkIcons = true
    )

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = primaryContainerDarkMediumContrast,
                contentColor = onPrimaryDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding(),
                elevation = 4.dp,
            ) {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Share Fit",
                        style =
                        MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryDark,
                        ),
                    )
                }
            }
        },
        content = {paddingValues ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                content()
            }
        }
    )
}

@Composable
fun Body(content: @Composable () -> Unit)  {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        content()
    }
}

@Composable
@Preview
fun HeaderPreview()  {
    Header {
        Body {
            Text("Hello")
        }
    }
}
