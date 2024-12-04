package com.example.fitbattleandroid.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitbattleandroid.R
import com.example.fitbattleandroid.ui.parts.common.Background
import com.example.fitbattleandroid.ui.parts.common.Header
import com.example.fitbattleandroid.ui.theme.FitBattleAndroidTheme

class PermissionsRationaleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitBattleAndroidTheme {
                HealthConnectPrivacyPolicyScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun HealthConnectPrivacyPolicyScreen(modifier: Modifier) {
    Background {
        Header(
            content = {
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
                            text = "ブライパシーポリシー",
                            fontSize = 24.sp,
                        )

                        Image(
                            painter = painterResource(R.drawable.logo_border),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .size(200.dp)
                                    .align(Alignment.Center),
                        )
                    }

                    TitleAndTextColumn(
                        title = "データの取得と管理",
                        text = "このアプリでは、ユーザーのフィットネスデータを取得し、アプリ内で管理可能にします。",
                    )

                    TitleAndTextColumn(
                        title = "データの共有",
                        text =
                            "アプリ内の設定により、フィットネスデータの共有を許可することで、データがサーバに保存されます。" +
                                "フィットネスデータを共有することで、アプリ内で他のユーザーとフィットネスデータを共有・比較することができます。",
                    )

                    TitleAndTextColumn(
                        title = "プライバシー保護",
                        text = "共有されたデータは、ユーザー同士のステータス表示のみに使用され、データから個人が特定されることはありません。",
                    )
                }
            },
        )
    }
}

@Composable
fun TitleAndTextColumn(
    title: String,
    text: String,
) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(text = text, fontSize = 17.sp)
    }
    Spacer(modifier = Modifier.padding(20.dp))
}

@Preview
@Composable
fun PreviewHealthConnectPrivacyPolicyScreen() {
    HealthConnectPrivacyPolicyScreen(modifier = Modifier.fillMaxSize())
}
