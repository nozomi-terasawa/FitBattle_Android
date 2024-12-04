package com.example.fitbattleandroid.ui.parts.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = null)
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
            ) {
                Text(text = "確認")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(text = "拒否")
            }
        },
    )
}

@Preview
@Composable
fun DialogPreview() {
    Dialog(
        onDismissRequest = { },
        onConfirmation = { },
        dialogTitle = "Title",
        dialogText = "Text",
        icon = Icons.Default.Info,
    )
}
