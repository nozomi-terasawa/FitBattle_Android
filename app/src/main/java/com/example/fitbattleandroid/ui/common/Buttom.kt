package com.example.fitbattleandroid.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast

@Composable
fun NormalBottom(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = primaryContainerDarkMediumContrast,
            ),
        modifier =
            Modifier
                .width(200.dp),
    ) {
        content()
    }
}

@Composable
fun TransparentBottom(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            ),
    ) {
        content()
    }
}

@Composable
@Preview
fun NormalBottomPreview() {
    NormalBottom({ /*Todo*/ }) {
        Text("Hello")
    }
    TransparentBottom({ /*Todo*/ }) {
        Text("Hello")
    }
}
