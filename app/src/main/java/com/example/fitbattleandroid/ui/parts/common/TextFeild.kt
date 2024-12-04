package com.example.fitbattleandroid.ui.parts.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitbattleandroid.ui.theme.onBackgroundLightMediumContrast
import com.example.fitbattleandroid.ui.theme.onSurfaceVariantLight
import com.example.fitbattleandroid.ui.theme.outlineLightMediumContrast

@Composable
fun CommonOutlinedTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorText: String? = null,
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            label = {
                Text(
                    text = label,
                    color = onSurfaceVariantLight, // プレースホルダー色
                )
            },
            supportingText = {
                // isError が true の場合にエラーメッセージを表示
                if (isError && errorText != null) {
                    Text(
                        text = errorText,
                        color = androidx.compose.ui.graphics.Color.Red,
                        fontSize = 12.sp,
                    )
                }
            },
            shape = RoundedCornerShape(5.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = outlineLightMediumContrast, // フォーカス時のボーダー色
                    unfocusedBorderColor = outlineLightMediumContrast, // 非フォーカス時のボーダー色
                    focusedTextColor = onBackgroundLightMediumContrast, // フォーカス時のテキスト色
                    unfocusedTextColor = onBackgroundLightMediumContrast, // 非フォーカス時のテキスト色
                ),
            modifier =
                Modifier
                    .width(300.dp)
                    .padding(bottom = 20.dp),
        )
    }
}
