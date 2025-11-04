package com.example.dietlens.feature.signup

import com.example.dietlens.feature.allergy.AllergyPreferences

data class User(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val allergies: AllergyPreferences? = null
)