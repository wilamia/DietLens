package com.example.dietlens.feature.scanner.data

import android.content.Context
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.dietlens.R
import com.example.dietlens.feature.scanner.data.AllergenKey

fun AllergenKey.toLocalizedName(context: Context): String {
    return when (this) {
        AllergenKey.GLUTEN -> context.getString(R.string.allergen_gluten)
        AllergenKey.LACTOSE -> context.getString(R.string.allergen_lactose)
        AllergenKey.NUTS -> context.getString(R.string.allergen_nuts)
        AllergenKey.SOY -> context.getString(R.string.allergen_soy)
        AllergenKey.EGGS -> context.getString(R.string.allergen_eggs)
        AllergenKey.SEAFOOD -> context.getString(R.string.allergen_seafood)
        AllergenKey.FRUITS -> context.getString(R.string.allergen_fruits)
    }
}

@Composable
fun DetectedAllergensText(allergens: List<AllergenKey>) {
    val context = LocalContext.current
    val text = allergens.joinToString(", ") { it.toLocalizedName(context) }
    Text(text = text)
}