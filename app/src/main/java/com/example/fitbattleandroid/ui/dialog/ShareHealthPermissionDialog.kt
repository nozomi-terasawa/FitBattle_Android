package com.example.fitbattleandroid.ui.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShareHealthDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
    shareHealthDataPermission: Boolean,
    setShareHealthDataPermission: (Boolean) -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Share, contentDescription = null)
        },
        title = {
            Text(text = "ヘルスデータの共有の許可")
        },
        text = {
            Column {
                Text(
                    text =
                        "ヘルスデータの共有を許可することで、他のユーザーへあなたのヘルスデータが共有されます。\n" +
                            "\nヘルスデータの共有を許可しない場合、すれ違い機能、ヘルスステータスの自己管理機能は利用できますが、" +
                            "他のユーザーにあなたのヘルスステータスを表示することはできません。",
                    fontSize = 17.sp,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "ヘルスデータの共有を許可",
                        fontSize = 17.sp,
                    )

                    Switch(
                        checked = shareHealthDataPermission,
                        onCheckedChange = {
                            setShareHealthDataPermission(it)
                        },
                        modifier = Modifier.padding(20.dp),
                    )
                }
            }
        },
        onDismissRequest = {
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                },
            ) {
                Text(text = "保存")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(text = "キャンセル")
            }
        },
    )
}

@Preview
@Composable
fun ShareHealthDialogPreview() {
    ShareHealthDialog(
        shareHealthDataPermission = true,
        setShareHealthDataPermission = {},
    )
}
