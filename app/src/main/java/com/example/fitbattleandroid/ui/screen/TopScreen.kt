package com.websarva.wings.android.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fitbattleandroid.R
import com.example.fitbattleandroid.ui.common.Background
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.common.NormalBottom
import com.example.fitbattleandroid.ui.common.NormalText

@Composable
fun TopScreen(navController: NavController) {
    Background {
        Header(
            content = {
                Image(
                    painter = painterResource(R.drawable.logo_border),
                    contentDescription = null,
                    modifier = Modifier.size(500.dp),
                )
                Column {
                    NormalBottom({ navController.navigate("login") }) {
                        NormalText("ログイン")
                    }

                    Spacer(modifier = Modifier.size(20.dp))

                    NormalBottom({ navController.navigate("regi") }) {
                        NormalText("登録する")
                    }
                }
            },
            actions = {},
        )
    }
}

@Composable
@Preview
fun TopScreenPreview() {
    TopScreen(navController = rememberNavController())
}
