package com.example.fitbattleandroid.ui.screen

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.fitbattleandroid.data.api.dto.geofence.MemberInfo
import com.example.fitbattleandroid.ui.parts.common.Background
import com.example.fitbattleandroid.ui.parts.common.Header
import com.example.fitbattleandroid.ui.theme.inversePrimaryLight
import com.example.fitbattleandroid.ui.theme.onPrimaryDark
import com.example.fitbattleandroid.ui.theme.primaryContainerLight
import com.example.fitbattleandroid.ui.viewmodel.GeofenceEntryState

@Composable
fun EncounterHistoryScreen(
    modifier: Modifier,
    geofenceEntryState: GeofenceEntryState,
) {
    Background {
        Header(
            content = {
                when (geofenceEntryState) {
                    is GeofenceEntryState.Loading -> {
                        Text(
                            text = "Loading...",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp),
                            color = onPrimaryDark,
                        )
                    }

                    is GeofenceEntryState.Success -> {
                        Text(
                            text = "今日であったユーザー",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp),
                            color = onPrimaryDark,
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(
                                items = geofenceEntryState.entryGeoFenceRes.passingMember,
                                key = { encounterUser -> encounterUser.id },
                            ) { encounterUser ->
                                EncounterHistoryItem(
                                    modifier = Modifier.fillMaxWidth(),
                                    encounterUser = encounterUser,
                                )
                            }
                        }
                    }

                    is GeofenceEntryState.Error -> {
                        Text(
                            text = "Error",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(20.dp),
                            color = onPrimaryDark,
                        )
                    }
                }
            },
            actions = {},
        )
    }
}

@Composable
fun EncounterHistoryItem(
    modifier: Modifier,
    encounterUser: MemberInfo,
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = inversePrimaryLight,
            ),
        modifier =
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(60.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(primaryContainerLight),
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = encounterUser.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(4.dp),
                    color = onPrimaryDark,
                )
                Text(
                    text = "昨日の総消費カロリー：1,700kcal",
                    color = onPrimaryDark,
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

@Preview
@Composable
fun EncounterHistoryScreenPreview() {
    EncounterHistoryScreen(
        modifier = Modifier.fillMaxSize(),
        geofenceEntryState = GeofenceEntryState.Loading,
    )
}
