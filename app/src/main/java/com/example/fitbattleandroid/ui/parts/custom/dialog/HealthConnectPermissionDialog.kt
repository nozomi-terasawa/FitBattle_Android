package com.example.fitbattleandroid.ui.parts.custom.dialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.fitbattleandroid.ui.parts.common.Dialog

@Composable
fun HealthConnectPermissionDialog(
    openDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val packageName = context.packageName

    if (openDialog) {
        Dialog(
            onDismissRequest = {
                setShowDialog(false)
            },
            onConfirmation = {
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                context.startActivity(intent)
                setShowDialog(false)
            },
            dialogTitle = "ヘルスコネクトの読み取りの許可",
            dialogText = "アプリの機能を使用するためには、ヘルスコネクトの読み取りの許可が必要です。",
            icon = Icons.Default.Info,
        )
    }
}
