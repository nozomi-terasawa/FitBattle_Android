package com.example.fitbattleandroid.ui.parts.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GrayZone() {
    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(200.dp),
    ) {
    }
}
