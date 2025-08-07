package com.example.dietlens.feature.allergy

data class AllergyPreferences(
    val gluten: Boolean = false,
    val lactose: Boolean = false,
    val nuts: Boolean = false,
    val seafood: Boolean = false,
    val eggs: Boolean = false,
    val soy: Boolean = false,
    val fruits: Boolean = false,
)
