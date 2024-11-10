package com.example.fitbattleandroid.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import com.example.fitbattleandroid.data.FitnessRemoteDataSource
import com.example.fitbattleandroid.repositoryImpl.SaveFitnessRepositoryImpl
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.common.ShowCurrentTimeAndRemainingTime
import com.example.fitbattleandroid.ui.dialog.HealthConnectPermissionDialog
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import com.example.fitbattleandroid.ui.theme.primaryContainerDarkMediumContrast
import com.example.fitbattleandroid.viewmodel.HealthConnectViewModel
import com.websarva.wings.android.myapplication.FireAnimeModule
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@Composable
fun FitnessMemory(
    modifier: Modifier,
    healthConnectClient: HealthConnectClient,
    calorieViewModel: HealthConnectViewModel,
) {
    val calorieUiState by remember { mutableStateOf(calorieViewModel.calorieUiState) }
    val currentCalorieStr = calorieUiState.value.calorie
    val scope = rememberCoroutineScope()

    // 取得開始時間
    val startLocalDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT) // 　今日の0時から
    val startEpochMilli = startLocalDateTime.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()

    // 取得終了時間
    val endLocalDateTime = LocalDateTime.now()
    val endEpochMilli = endLocalDateTime.toInstant(ZoneOffset.ofHours(9)).toEpochMilli()

    val permissions =
        setOf(
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
        )

    val showHealthConnectDialog = remember { mutableStateOf(false) }

    val healthConnectPermissionLauncher =
        rememberLauncherForActivityResult(
            PermissionController.createRequestPermissionResultContract(),
        ) { isGranted ->
            if (isGranted.containsAll(permissions)) {
            } else {
                // 権限が拒否された場合
                showHealthConnectDialog.value = true
            }
        }

    Header(
        content = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .imePadding(),
            ) {
                LaunchedEffect(Unit) {
                    val granted =
                        healthConnectClient.permissionController.getGrantedPermissions()

                    if (granted.containsAll(permissions)) {
                        calorieViewModel.readCalorie(
                            healthConnectClient = healthConnectClient,
                            startTime = Instant.ofEpochMilli(startEpochMilli),
                            endTime = Instant.ofEpochMilli(endEpochMilli),
                        )
                    } else {
                        healthConnectPermissionLauncher.launch(permissions)
                    }
                }

                if (showHealthConnectDialog.value) {
                    HealthConnectPermissionDialog(
                        openDialog = true,
                        setShowDialog = { boolean ->
                            showHealthConnectDialog.value = boolean
                        },
                    )
                }
                Box {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        // 画面全体を使用
                        verticalArrangement = Arrangement.Center, // 子要素を上から配置
                        horizontalAlignment = Alignment.CenterHorizontally, // 子要素を左揃え
                    ) {
                        Text(
                            text = "消費カロリー",
                            fontSize = 22.sp, // 大きなフォントサイズ
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryDark,
                        )
                        Text(
                            text = "today", // ヘッダーのテキスト
                            fontSize = 35.sp, // 大きなフォントサイズ
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryDark,
                            modifier =
                                Modifier
                                    .padding(top = 10.dp), // ヘッダーの上部に余白を追加
                        )
                        CalorieMeter(
                            modifier = Modifier,
                            max = 10000f,
                            progress = currentCalorieStr.toFloat(),
                            currentCalorieStr = currentCalorieStr,
                            // .padding(90.dp)
                        )
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.size(550.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    calorieViewModel.readCalorie(
                                        healthConnectClient = healthConnectClient,
                                        startTime = Instant.ofEpochMilli(startEpochMilli),
                                        endTime = Instant.ofEpochMilli(endEpochMilli),
                                    )
                                }
                            },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = primaryContainerDarkMediumContrast,
                                ),
                        ) {
                            Text(
                                text = "最新のカロリーを取得",
                                fontSize = 20.sp,
                                color = onPrimaryDark,
                            )
                        }

                        ShowCurrentTimeAndRemainingTime(modifier)

                        Text(
                            modifier =
                                Modifier
                                    .padding(top = 10.dp)
                                    .align(Alignment.CenterHorizontally),
                            text = "$currentCalorieStr kcal",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = onPrimaryDark,
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = onPrimaryDark,
                )
            }
        },
    )
}

@Composable
fun CalorieMeter(
    modifier: Modifier,
    max: Float,
    progress: Float,
    currentCalorieStr: String,
) {
    val circleAngle = 360f
    val angle = 240f
    val progressWidth = 24.dp
    val backgroundWidth = 24.dp
    val startAngle = (circleAngle / 4) + ((circleAngle - angle) / 2)

    Box(
        modifier =
            Modifier
                .padding(top = 100.dp, bottom = 100.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier =
                Modifier
                    .size(250.dp)
                    .padding(10.dp),
            onDraw = {
                // 外枠を描画
                drawArc(
                    color = onPrimaryDark,
                    startAngle = startAngle,
                    sweepAngle = angle,
                    useCenter = false,
                    style =
                        Stroke(
                            width = backgroundWidth.toPx(),
                            cap = StrokeCap.Round,
                        ),
                    size = Size(size.width, size.height),
                    // topLeft = Offset(4f, 4f),// 少しずらして影にする
                )
                // 背景を描画
                drawArc(
                    brush =
                        Brush.radialGradient(
                            colors = listOf(Color(192, 192, 192), Color(127, 192, 188)),
                            center = Offset(size.width / 2, size.height / 2),
                            radius = size.width / 2,
                        ),
                    startAngle = startAngle,
                    sweepAngle = angle,
                    useCenter = false,
                    style = Stroke(width = backgroundWidth.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height),
                )
                // 進捗を描画
                // 進捗部分にグラデーションをかけて立体的なエフェクト
                drawArc(
                    brush =
                        Brush.sweepGradient(
                            colors = listOf(Color(33, 108, 94), Color(80, 200, 180)),
                            center = Offset(size.width / 2, size.height / 2),
                        ),
                    startAngle = startAngle,
                    sweepAngle = angle / max * progress,
                    useCenter = false,
                    style = Stroke(width = progressWidth.toPx(), cap = StrokeCap.Round),
                    size = Size(size.width, size.height),
                )
//                    // 光沢のハイライトを追加して円の上部に光が当たっているように見せる
//                    drawArc(
//                        brush = Brush.linearGradient(
//                            colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
//                            start = Offset(size.width / 2, 0f),
//                            end = Offset(size.width / 3, size.height / 2),
//                        ),
//                        startAngle = startAngle - 5, // ハイライトを調整
//                        sweepAngle = angle / 1,
//                        useCenter = false,
//                        style = Stroke(width = progressWidth.toPx() / 2, cap = StrokeCap.Round),
//                        size = Size(size.width, size.height),
//                    )
            },
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FireAnimeModule()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "$currentCalorieStr/${max.toInt()}.0kcal",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = onPrimaryDark,
                modifier = Modifier.padding(top = 8.dp), // 画像の下に少しスペースを追加
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun FitnessMemoryPreview() {
    val context = LocalContext.current

    FitnessMemory(
        modifier = Modifier,
        healthConnectClient = HealthConnectClient.getOrCreate(context),
        calorieViewModel =
            HealthConnectViewModel(
                SaveFitnessRepositoryImpl(
                    FitnessRemoteDataSource(),
                ),
            ),
    )
}
