package com.example.fitbattleandroid.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitbattleandroid.R
import com.example.fitbattleandroid.ui.common.Background
import com.example.fitbattleandroid.ui.common.Body
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.common.NormalBottom

@Composable
fun LocationRationaleScreen(
    modifier: Modifier,
    onNavigateMapScreen: () -> Unit,
) {
    val locationRationaleText =
        buildString {
            append("アプリのすれ違い機能では位置情報を使用します。")
            append("すれ違い機能を活用するため、以下の通りに権限を付与することをおすすめします。")
        }

    Background {
        Header {
            Body {
                Column(
                    modifier =
                        modifier
                            .fillMaxSize()
                            .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.TopCenter),
                            text = "位置情報の許可",
                            fontSize = 24.sp,
                        )

                        Image(
                            painter = painterResource(R.drawable.title),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .size(200.dp)
                                    .align(Alignment.Center),
                        )
                    }

                    Text(
                        text = locationRationaleText,
                        fontSize = 17.sp,
                    )

                    Spacer(modifier = Modifier.padding(20.dp))

                    IconAndTextRow(
                        text = "正確な位置情報を使用します。おおよその位置情報のみが付与された場合、その精度によりすれ違い機能がうまく作動しない可能性があります。",
                        icon = Icons.Default.LocationOn,
                    )

                    // TODO "常に許可"をbackgroundPermissionOptionLabelに変更
                    IconAndTextRow(
                        text = "位置情報は「常に許可」を設定します。常に許可では、アプリを終了してもすれ違い機能を維持します。それ以外の設定はすれ違い機能が使用できません。",
                        icon = Icons.Default.CheckCircle,
                    )

                    Text(
                        text = "上記を確認し、アプリの誘導に従がって位置情報を許可してください。設定は後から変更できます。",
                        fontSize = 20.sp,
                    )

                    Spacer(modifier = Modifier.padding(20.dp))

                    NormalBottom(
                        onClick = {
                            onNavigateMapScreen()
                        },
                    ) {
                        Text(
                            text = "アプリに進む",
                            fontSize = 20.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconAndTextRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Icon(
            modifier =
                Modifier
                    .size(25.dp),
            imageVector = icon,
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.DarkGray,
        )

        Spacer(modifier = Modifier.size(5.dp))

        Text(
            text = text,
            fontSize = 16.sp,
        )
    }

    Spacer(modifier = Modifier.padding(20.dp))
}

@Preview
@Composable
fun LocationRationaleScreenPreview() {
    LocationRationaleScreen(Modifier, {})
}
