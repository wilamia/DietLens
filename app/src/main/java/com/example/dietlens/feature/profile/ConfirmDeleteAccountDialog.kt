package com.example.dietlens.feature.profile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary

@Composable
fun ConfirmDeleteAccountDialog(
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = OnPrimary,
        title = { Text("Удалить аккаунт?", color = Buttons) },
        text = {
            Text(
                "Это действие необратимо: ваш профиль и данные будут удалены. Продолжить?",
                color = Buttons
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Buttons
                )
            ) {
                Text(if (isLoading) "Удаляю..." else "Удалить",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton (
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Отмена",
                    color = Buttons)
            }
        }
    )
}
