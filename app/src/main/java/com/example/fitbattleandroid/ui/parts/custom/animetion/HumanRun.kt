package com.websarva.wings.android.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.R
import kotlinx.coroutines.delay

@Composable
fun AnimemotionModule() {
    var alpha by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(300f) }
    var fadeOut by remember { mutableStateOf(true) }
    var smokeAlpha by remember { mutableStateOf(1f) }
    var smokeOut by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(50)
            if (fadeOut) {
                alpha -= 0.04f
                offsetX -= 20f
                if (alpha <= 0f) {
                    alpha = 0f
                    fadeOut = false
                    offsetX = 350f
                }
            } else {
                alpha += 0.04f
                offsetX -= 20f
                if (alpha >= 1f) {
                    alpha = 1f
                    fadeOut = true
                }
            }
            if (smokeOut) {
                smokeAlpha -= 0.1f
                if (smokeAlpha <= 0f) {
                    smokeAlpha = 0f
                    smokeOut = false
                }
            } else {
                smokeAlpha += 0.1f
                if (smokeAlpha >= 1f) {
                    smokeAlpha = 1f
                    smokeOut = true
                }
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            val image = painterResource(R.drawable.pngtreerunning_character_silhouette_png_free_4627520)
            val smoke = painterResource(R.drawable.pngtreesmoke_white_dream_fog_6860988)

            Box {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .graphicsLayer {
                                this.alpha = alpha
                                translationX = offsetX
                                translationY = 0f
                            }.size(100.dp),
                )
                Image(
                    painter = smoke,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .graphicsLayer {
                                this.alpha = smokeAlpha
                                translationX = offsetX + 155f
                                translationY = 40f
                            }.size(100.dp),
                )
                Image(
                    painter = smoke,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .graphicsLayer {
                                this.alpha = 1f - smokeAlpha
                                translationX = offsetX + 85f
                                translationY = 40f
                            }.size(100.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun AnimeModulePreview() {
    AnimemotionModule()
}
