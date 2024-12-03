package com.example.fitbattleandroid.ui.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CurrentLocationMarker() {
    Canvas(modifier = Modifier.size(100.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val circleBorder = 3.dp.toPx()
        val circleRadius = 6.dp.toPx()
        val gradient =
            Brush.radialGradient(
                colorStops =
                    arrayOf(
                        0.0f to Blue,
                        0.2f to Blue.copy(alpha = 0.3f),
                        1f to Color.Transparent,
                    ),
            ) // 外側に行くほど透明にする
        drawArc(
            brush = gradient,
            startAngle = 235f, // 孤の描画開始角度。3時が0度
            sweepAngle = 70f, // 何度描画するか。ここでは235度から305度の弧になる。
            useCenter = true,
            size = Size(size.width, size.height + circleRadius * 2 + circleBorder * 2), // 孤の中心点が円のbottomにくるようにする
        )

        drawCircle(
            center = center,
            color = Color.White,
            radius = circleRadius + circleBorder,
        )
        drawCircle(
            center = center,
            color = Blue,
            radius = circleRadius,
        )
    }
}

@Preview
@Composable
fun CurrentLocationMarkerPreview() {
    CurrentLocationMarker()
}
