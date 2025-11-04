package com.example.dietlens.feature.scanner.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dietlens.R
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.Gray20

@Composable
fun InputView(
    barcode: String,
    onBarcodeChange: (String) -> Unit,
    onCheckProduct: () -> Unit,
    isLoading: Boolean
) {
    Column {
        OutlinedTextField(
            value = barcode,
            onValueChange = onBarcodeChange,
            label = { Text(stringResource(R.string.enter_barcode), color = Buttons) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Buttons,
                unfocusedBorderColor = Buttons.copy(alpha = .5f),
                disabledBorderColor = Buttons.copy(alpha = .3f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedLabelColor = Buttons,
                cursorColor = Buttons,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface

            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCheckProduct,
            enabled = !isLoading && barcode.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Buttons,
                disabledContainerColor = Gray20
            )
        ) {
            Text(if (isLoading) "Загрузка..." else stringResource(R.string.check_product))
        }
    }
}