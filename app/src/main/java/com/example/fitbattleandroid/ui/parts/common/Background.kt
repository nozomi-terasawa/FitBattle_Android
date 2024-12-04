package com.example.fitbattleandroid.ui.parts.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.fitbattleandroid.ui.theme.primaryContainerLight

@Composable
fun Background(content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .fillMaxSize()
                .background(primaryContainerLight),
    ) {
        content()
    }
}

@Composable
@Preview
fun BackgroundPreview() {
    Background { Text("Hello") }
}
