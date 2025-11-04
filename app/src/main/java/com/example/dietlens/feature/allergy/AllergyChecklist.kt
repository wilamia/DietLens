package com.example.dietlens.feature.allergy

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun AllergyChecklist(
    items: List<AllergyItem>,
    checks: Map<String, Boolean>,
    onCheckChange: (key: String, value: Boolean) -> Unit
) {
    items.forEach { item ->
        AllergyCheckbox(
            label = stringResource(id = item.titleRes),
            checked = checks[item.key] == true,
            onCheckedChange = { onCheckChange(item.key, it) }
        )
    }
}