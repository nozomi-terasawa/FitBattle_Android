package com.example.fitbattleandroid.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.repositoryImpl.AuthRepositoryImpl
import com.example.fitbattleandroid.ui.common.Background
import com.example.fitbattleandroid.ui.common.CommonOutlinedTextField
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.common.MinText
import com.example.fitbattleandroid.ui.common.NormalBottom
import com.example.fitbattleandroid.ui.common.NormalText
import com.example.fitbattleandroid.ui.common.TitleText
import com.example.fitbattleandroid.ui.common.TransparentBottom
import com.example.fitbattleandroid.viewmodel.AuthState
import com.example.fitbattleandroid.viewmodel.AuthViewModel
import com.example.fitbattleandroid.viewmodel.toUserLoginReq
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateMain: () -> Unit,
    onNavigateRegi: () -> Unit,
    authViewModel: AuthViewModel,
) {
    val loginState = authViewModel.loginState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Background {
        Header(
            content = {
                TitleText("ログイン")

                Spacer(modifier = Modifier.size(50.dp))

                CommonOutlinedTextField(
                    value = loginState.email,
                    label = "メールアドレス",
                    onValueChange = { newValue ->
                        authViewModel.updateLoginTextField("email", newValue)
                    },
                )

                CommonOutlinedTextField(
                    value = loginState.password,
                    label = "パスワード",
                    onValueChange = { newValue ->
                        authViewModel.updateLoginTextField("password", newValue)
                    },
                )

                NormalBottom(
                    onClick = {
                        scope.launch {
                            val authResult =
                                authViewModel.login(authViewModel.loginState.toUserLoginReq())
                            when (authResult) {
                                is AuthState.Loading -> {}
                                is AuthState.Success -> {
                                    scope.launch {
                                        authViewModel.saveAuthToken(
                                            context,
                                            authResult.token,
                                        )
                                        onNavigateMain()
                                    }
                                }

                                is AuthState.Error -> {}
                                else -> {}
                            }
                        }
                    },
                ) {
                    NormalText("ログイン")
                }

                TransparentBottom(
                    onClick = onNavigateRegi,
                ) {
                    MinText("新規登録の方はこちら")
                }
            },
            actions = {},
        )
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateMain = {},
        onNavigateRegi = {},
        authViewModel =
            AuthViewModel(
                LocalContext.current.applicationContext as Application,
                AuthRepositoryImpl(),
            ),
    )
}
