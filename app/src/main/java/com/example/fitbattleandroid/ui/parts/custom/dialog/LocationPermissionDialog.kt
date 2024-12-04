package com.example.fitbattleandroid.ui.parts.custom.dialog

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.fitbattleandroid.ui.parts.common.Dialog

private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 1

@Composable
fun RequestLocationPermissionDialog(
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
            dialogTitle = "位置情報の許可",
            dialogText = "アプリの機能を使用するためには、位置情報の許可が必要です。",
            icon = Icons.Default.Info,
        )
    }
}

@Composable
fun UpdateLocationPermissionDialog(
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
            dialogTitle = "正確な位置情報の許可",
            dialogText = "おおよその位置情報を使用すると、精度によりすれ違い機能がうまく作動しない可能性があります。すれ違い機能を正確に使用するには、正確な位置情報を許可することをおすすめします。",
            icon = Icons.Default.Info,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun RequestBackgroundLocationPermissionDialog(
    openDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    if (openDialog) {
        Dialog(
            onDismissRequest = {
                setShowDialog(false)
            },
            onConfirmation = {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE,
                )
                setShowDialog(false)
            },
            dialogTitle = "バックグラウンドでの位置情報の許可",
            dialogText = "アプリの機能を使用するためには、位置情報を「常に許可」にする必要があります。",
            icon = Icons.Default.Info,
        )
    }
}
