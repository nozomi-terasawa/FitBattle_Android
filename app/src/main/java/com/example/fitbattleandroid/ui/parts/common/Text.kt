package com.example.fitbattleandroid.ui.parts.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.fitbattleandroid.ui.theme.onPrimaryDark

@Composable
fun NormalText(text: String) {
    Column {
        Text(
            text = text,
            color = onPrimaryDark,
        )
    }
}

@Composable
fun MinText(text: String) {
    Column {
        Text(
            text = text,
            fontSize = 10.sp,
            color = onPrimaryDark,
        )
    }
}

@Composable
fun TitleText(text: String) {
    Column {
        Text(
            text = text,
            color = onPrimaryDark,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
@Preview
fun NormalTextPreview() {
    NormalText("Hello")
    MinText("Hello")
}
