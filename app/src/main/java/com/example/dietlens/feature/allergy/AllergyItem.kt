package com.example.dietlens.feature.allergy

import com.example.dietlens.R

data class AllergyItem(
    val key: String,
    val titleRes: Int
)

val ALLERGY_ITEMS = listOf(
    AllergyItem("gluten",  R.string.allergy_gluten),
    AllergyItem("lactose", R.string.allergy_lactose),
    AllergyItem("nuts",    R.string.allergy_nuts),
    AllergyItem("seafood", R.string.allergy_seafood),
    AllergyItem("eggs",    R.string.allergy_eggs),
    AllergyItem("soy",     R.string.allergy_soy),
    AllergyItem("fruits",  R.string.allergy_fruits),
)