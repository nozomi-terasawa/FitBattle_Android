package com.websarva.wings.android.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun FireAnimeModule() {
    val fireParticles = remember { mutableStateListOf<FireParticleData>() }

    LaunchedEffect(Unit) {
        while (true) {
            fireParticles.add(FireParticleData())
            delay(100)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
        Modifier,
//                .fillMaxSize()
    ) {
        val image = painterResource(R.drawable.pngtreeburning_fire_5637806)
        val fire = painterResource(R.drawable.pngtreeburning_fire_5637806)

        var fireOffsetX by remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                fireOffsetX = Random.nextFloat() * 5f - 5f
                delay(100)
            }
        }

        Image(
            painter = fire,
            contentDescription = null,
            modifier =
                Modifier
                    .size(150.dp)
                    .graphicsLayer(
                        translationX = fireOffsetX,
                    ),
        )

        fireParticles.forEach { particle ->
            FireParticle(image = image, data = particle) {
                fireParticles.remove(particle)
            }
        }
    }
}

@Composable
fun FireParticle(
    image: androidx.compose.ui.graphics.painter.Painter,
    data: FireParticleData,
    onDisapper: () -> Unit,
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(1f) }

    LaunchedEffect(Unit) {
        while (alpha > 0f) {
            offsetX += (cos(data.angle) * data.speed).toFloat()
            offsetY += (sin(data.angle) * data.speed).toFloat() + 1
            alpha -= 0.02f

            delay(50)
        }
        onDisapper()
    }

    Image(
        painter = image,
        contentDescription = null,
        modifier =
            Modifier
                .size(7.dp)
                .graphicsLayer(
                    translationX = offsetX,
                    translationY = offsetY - 50f,
                    scaleX = 1f,
                    scaleY = 1f,
                    alpha = alpha,
                ),
    )
}

data class FireParticleData(
    val angle: Double = Random.nextDouble(0.0, 2 * Math.PI),
    val speed: Float = Random.nextFloat() * 5 + 2,
)

@Preview
@Composable
fun FireAnimeModulePreview() {
    FireAnimeModule()
}
