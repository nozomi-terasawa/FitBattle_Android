package com.example.fitbattleandroid.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.data.repositoryImpl.AuthRepositoryImpl
import com.example.fitbattleandroid.ui.parts.common.Background
import com.example.fitbattleandroid.ui.parts.common.CommonOutlinedTextField
import com.example.fitbattleandroid.ui.parts.common.Header
import com.example.fitbattleandroid.ui.parts.common.MinText
import com.example.fitbattleandroid.ui.parts.common.NormalBottom
import com.example.fitbattleandroid.ui.parts.common.NormalText
import com.example.fitbattleandroid.ui.parts.common.TitleText
import com.example.fitbattleandroid.ui.parts.common.TransparentBottom
import com.example.fitbattleandroid.ui.viewmodel.AuthState
import com.example.fitbattleandroid.ui.viewmodel.AuthViewModel
import com.example.fitbattleandroid.ui.viewmodel.toUserLoginReq
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

    // エラー表示用の状態
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }

    Background {
        Header(
            content = {
                TitleText("ログイン")

                Spacer(modifier = Modifier.size(50.dp))

                // メールアドレス入力フィールド
                CommonOutlinedTextField(
                    value = loginState.email,
                    label = "メールアドレス",
                    onValueChange = { newValue ->
                        authViewModel.updateLoginTextField("email", newValue)
                    },
                    isError = showEmailError,
                    errorText = "メールアドレスを入力してください",
                )

                // パスワード入力フィールド
                CommonOutlinedTextField(
                    value = loginState.password,
                    label = "パスワード",
                    onValueChange = { newValue ->
                        authViewModel.updateLoginTextField("password", newValue)
                    },
                    // パスワードが空白の場合にエラーメッセージを表示
                    isError = showPasswordError,
                    errorText = "パスワードを入力してください",
                )
                NormalBottom(
                    onClick = {
                        // 入力チェック
                        showEmailError = loginState.email.isBlank()
                        showPasswordError = loginState.password.isBlank()

                        // エラーがない場合にログイン処理を実行
                        if (!showEmailError && !showPasswordError) {
                            scope.launch {
                                val authResult = authViewModel.login(authViewModel.loginState.toUserLoginReq())
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
                                    AuthState.Initial -> TODO()
                                }
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
