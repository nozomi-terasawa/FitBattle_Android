package com.example.fitbattleandroid.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.fitbattleandroid.data.MemberInfo
import com.example.fitbattleandroid.viewmodel.HealthDataApiViewModel

@Composable
fun EncounterHistoryScreen(
    modifier: Modifier,
    dataAPIViewModel: HealthDataApiViewModel,
    list: List<MemberInfo> = emptyList(),
    // encounterHistoryList: List<EncounterUser>,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "今日であったユーザー",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = list,
                key = { encounterUser -> encounterUser.id },
            ) { encounterUser ->
                EncounterHistoryItem(
                    modifier = Modifier.fillMaxWidth(),
                    encounterUser = encounterUser,
                )
            }
        }
    }
}

@Composable
fun EncounterHistoryItem(
    modifier: Modifier,
    encounterUser: MemberInfo,
) {
    Log.d("result", encounterUser.toString())

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier =
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(8.dp),
        ) {
            // 本番はCoilに変更
            // UserIcon(imageUrl = encounterUser.userIcon)
            Box(
                modifier =
                    Modifier
                        .size(60.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                // .background(MaterialTheme.colorScheme.background)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = encounterUser.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp),
                )
                Text(
                    text = "昨日の総消費カロリー：1,700kcal",
                )
            }
        }
    }
}

@Composable
fun UserIcon(imageUrl: String) {
    AsyncImage(
        model =
            ImageRequest
                .Builder(LocalContext.current)
                .data(imageUrl)
                .build(),
        contentDescription = null,
        modifier =
            Modifier
                .size(60.dp)
                .clip(CircleShape),
    )
}

@Preview
@Composable
fun EncounterHistoryItemPreview() {
    EncounterHistoryItem(
        modifier = Modifier.fillMaxSize(),
        encounterUser =
            MemberInfo(
                id = 1,
                name = "フィットネス 太郎",
                iconUrl = "icon1",
            ),
    )
}

/*
@Preview
@Composable
fun EncounterHistoryScreenPreview() {
    EncounterHistoryScreen(
        modifier = Modifier.fillMaxSize(),
        encounterHistoryList =
            listOf(
                EncounterUser(
                    userId = "1",
                    userName = "フィットネス 太郎",
                    userIcon = "icon1",
                    calorie = 100,
                ),
                EncounterUser(
                    userId = "2",
                    userName = "フィットネス 花子",
                    userIcon = "icon2",
                    calorie = 200,
                ),
                EncounterUser(
                    userId = "3",
                    userName = "フィット・ネス次郎",
                    userIcon = "icon3",
                    calorie = 300,
                ),
            ),
    )
}

data class EncounterUser(
    val userId: String,
    val userName: String,
    val userIcon: String,
    val calorie: Int,
)

 */
