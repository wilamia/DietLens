package com.example.dietlens.feature.profile.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dietlens.R
import com.example.dietlens.theme.Buttons


@Composable
fun ChangeNameSheet(
    currentName: String,
    isLoading: Boolean,
    onConfirm: (String) -> Unit,
    onClose: () -> Unit,
    error: String?
) {
    var name by remember(currentName) { mutableStateOf(currentName) }

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.change_name),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.padding(4.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )
        if (!error.isNullOrBlank()) {
            Spacer(Modifier.padding(4.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.padding(4.dp))
        Button(
            onClick = { onConfirm(name.trim()) },
            enabled = !isLoading && name.trim().isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Buttons)
        ) {
            Text(
                text = if (isLoading)
                    stringResource(R.string.saving_profile)
                else
                    stringResource(R.string.save_profile)
            )
        }

        OutlinedButton(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.profile_cancel), color = Buttons)
        }
        Spacer(Modifier.padding(bottom = 8.dp))
    }
}
