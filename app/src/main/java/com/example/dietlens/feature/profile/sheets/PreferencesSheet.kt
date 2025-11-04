package com.example.dietlens.feature.profile.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.dietlens.feature.allergy.ALLERGY_ITEMS
import com.example.dietlens.feature.allergy.AllergyChecklist
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary

@Composable
fun PreferencesSheet(
    initialChecks: Map<String, Boolean>,
    isSaving: Boolean,
    onSave: (Map<String, Boolean>) -> Unit,
    onClose: () -> Unit,
    error: String?
) {
    var checks by remember(initialChecks) {
        mutableStateOf(
            if (initialChecks.isNotEmpty()) initialChecks
            else ALLERGY_ITEMS.associate { it.key to false }
        )
    }

    Column(Modifier
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .background(OnPrimary)) {
        Text(text = stringResource(R.string.profile_preferences_title), style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.padding(6.dp))

        AllergyChecklist(
            items = ALLERGY_ITEMS,
            checks = checks,
            onCheckChange = { key, value ->
                checks = checks.toMutableMap().apply { this[key] = value }
            }
        )

        if (!error.isNullOrBlank()) {
            Spacer(Modifier.padding(6.dp))
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.padding(6.dp))
        Button(
            onClick = { onSave(checks) },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Buttons) // ✅ цвет тут
        ) {
            Text(if (isSaving) stringResource(R.string.saving_profile) else stringResource(R.string.save_profile))
        }

        OutlinedButton (
            onClick = onClose,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.profile_cancel), color = Buttons)
        }
        Spacer(Modifier.padding(bottom = 4.dp))
    }
}
