package com.example.fitbattleandroid.ui.parts.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowCurrentTimeAndRemainingTime(modifier: Modifier) {
    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    // 毎秒現在時刻を更新する
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now() // 現在時刻を取得
            kotlinx.coroutines.delay(1000) // 1秒待機
        }
    }

    val endOfDay = currentTime.toLocalDate().atStartOfDay().plusDays(1) // 24時の時刻を取得
    val remainingSeconds = ChronoUnit.SECONDS.between(currentTime, endOfDay) // 残り秒数を計算

    val currentHour = currentTime.hour
    val currentMinute = currentTime.minute
    val currentSecond = currentTime.second

    val hours = remainingSeconds / 3600 // 残り時間の時間
    val minutes = (remainingSeconds % 3600) / 60 // 残り時間の分
    val seconds = remainingSeconds % 60 // 残り時間の秒

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 現在の時刻を表示
//        Text(
//            text = "現在の時刻: $currentHour:$currentMinute:$currentSecond",
//            fontSize = 30.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.Black,
//        )

        // 残り時間を表示
        Text(
            text = String.format("リセットまで %02d:%02d:%02d", hours, minutes, seconds),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = onPrimaryDark,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ShowCurrentTimeAndRemainingTimePreview() {
    ShowCurrentTimeAndRemainingTime(modifier = Modifier)
}
