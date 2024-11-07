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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fitbattleandroid.repositoryImpl.AuthRepositoryImpl
import com.example.fitbattleandroid.ui.common.Background
import com.example.fitbattleandroid.ui.common.Body
import com.example.fitbattleandroid.ui.common.CommonOutlinedTextField
import com.example.fitbattleandroid.ui.common.Header
import com.example.fitbattleandroid.ui.common.MinText
import com.example.fitbattleandroid.ui.common.NormalBottom
import com.example.fitbattleandroid.ui.common.NormalText
import com.example.fitbattleandroid.ui.common.TitleText
import com.example.fitbattleandroid.ui.common.TransparentBottom
import com.example.fitbattleandroid.ui.navigation.Screen
import com.example.fitbattleandroid.viewmodel.AuthState
import com.example.fitbattleandroid.viewmodel.AuthViewModel
import com.example.fitbattleandroid.viewmodel.toUserCreateReq
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    val registerState = authViewModel.registerState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Background {
        Header {
            Body {
                TitleText("新規登録")

                Spacer(modifier = Modifier.size(50.dp))

                CommonOutlinedTextField(
                    value = registerState.email,
                    onValueChange = { newValue ->
                        authViewModel.updateRegisterTextField("email", newValue)
                    },
                    label = "メールアドレス",
                )

                CommonOutlinedTextField(
                    value = registerState.password,
                    label = "パスワード",
                    onValueChange = { newValue ->
                        authViewModel.updateRegisterTextField("password", newValue)
                    },
                )

                CommonOutlinedTextField(
                    value = registerState.userName,
                    label = "名前",
                    onValueChange = { newValue ->
                        authViewModel.updateRegisterTextField("userName", newValue)
                    },
                )

                NormalBottom(
                    onClick = {
                        scope.launch {
                            val authResult =
                                authViewModel.register(
                                    userCreateReq = registerState.toUserCreateReq(),
                                )
                            when (authResult) {
                                is AuthState.Success -> {
                                    navController.navigate("main")
                                    authViewModel.saveAuthToken(
                                        context,
                                        authResult.token,
                                    )
                                }
                                else -> {}
                            }
                        }
                    },
                ) {
                    NormalText("新規登録")
                }

                TransparentBottom(
                    {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Regi.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                ) {
                    MinText("登録済みの方はこちら")
                }
            }
        }
    }
}

@Preview
@Composable
fun RegistrationScreenPreview(modifier: Modifier = Modifier) {
    RegistrationScreen(
        navController = rememberNavController(),
        authViewModel =
            AuthViewModel(
                LocalContext.current.applicationContext as Application,
                AuthRepositoryImpl(),
            ),
        modifier = modifier,
    )
}
