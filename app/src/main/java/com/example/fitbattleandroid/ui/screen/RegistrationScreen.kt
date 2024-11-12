package com.example.fitbattleandroid.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitbattleandroid.MyApplication
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
import com.example.fitbattleandroid.viewmodel.toUserCreateReq
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    onNavigateMain: () -> Unit,
    onNavigateLogin: () -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val registerState = authViewModel.registerState
    val context = LocalContext.current
    val applicationContext = context.applicationContext as MyApplication
    val scope = rememberCoroutineScope()

    // エラー表示用の状態
    var showEmailError by remember { mutableStateOf(false) }
    var showPasswordError by remember { mutableStateOf(false) }
    var showNameError by remember { mutableStateOf(false) }

    Background {
        Header(
            content = {
                TitleText("新規登録")

                Spacer(modifier = Modifier.size(50.dp))

                // メールアドレス入力フィールド
                CommonOutlinedTextField(
                    value = registerState.email,
                    onValueChange = { newValue ->
                        authViewModel.updateRegisterTextField("email", newValue)
                    },
                    label = "メールアドレス",
                    isError = showEmailError,
                    errorText = if (showEmailError) "メールアドレスを入力してください" else null,
                )

                // パスワード入力フィールド
                CommonOutlinedTextField(
                    value = registerState.password,
                    label = "パスワード",
                    onValueChange = { newValue ->
                        authViewModel.updateRegisterTextField("password", newValue)
                    },
                    isError = showPasswordError,
                    errorText = if (showPasswordError) "パスワードを入力してください" else null,
                )

                // 名前入力フィールド
                CommonOutlinedTextField(
                    value = registerState.userName,
                    label = "名前",
                    onValueChange = { newValue ->
                        authViewModel.updateRegisterTextField("userName", newValue)
                    },
                    isError = showNameError,
                    errorText = if (showNameError) "名前を入力してください" else null,
                )

                NormalBottom(
                    onClick = {
                        // 入力チェック
                        showEmailError = registerState.email.isBlank()
                        showPasswordError = registerState.password.isBlank()
                        showNameError = registerState.userName.isBlank()

                        // エラーがない場合に登録処理を実行
                        if (!showEmailError && !showPasswordError && !showNameError) {
                            scope.launch {
                                val authResult =
                                    authViewModel.register(
                                        userCreateReq = registerState.toUserCreateReq(),
                                    )
                                when (authResult) {
                                    is AuthState.Success -> {
                                        scope.launch {
                                            authViewModel.saveAuthToken(
                                                applicationContext,
                                                authResult.token,
                                            )
                                            onNavigateMain()
                                        }
                                    }
                                    else -> {}
                                }
                            }
                        }
                    },
                ) {
                    NormalText("新規登録")
                }

                TransparentBottom(
                    { onNavigateLogin() },
                ) {
                    MinText("登録済みの方はこちら")
                }
            },
            actions = {},
        )
    }
}

@Preview
@Composable
fun RegistrationScreenPreview(modifier: Modifier = Modifier) {
    RegistrationScreen(
        onNavigateMain = {},
        onNavigateLogin = {},
        authViewModel =
            AuthViewModel(
                LocalContext.current.applicationContext as Application,
                AuthRepositoryImpl(),
            ),
        modifier = modifier,
    )
}
