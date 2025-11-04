package com.example.dietlens.feature.restaurants

import com.google.android.gms.maps.model.LatLng

data class Restaurant(
    val id: String,
    val name: String,
    val position: LatLng,
    val allergies: List<Allergy>,
    val photoUrl: String?,

    // --- НОВЫЕ ПОЛЯ ---
    val address: String?,             // "Rakowiecka 41, Warszawa"
    val rating: Double?,              // 3.5
    val userRatingsTotal: Int?,     // 1284
    val isOpenNow: Boolean?,          // false
    val priceLevel: Int?,             // 2
    val phoneNumber: String?,         // "+48 22 625 76 27"
    val businessStatus: String?,
    val websiteUrl: String? = null
)

enum class Allergy {
    GLUTEN, NUTS, VEGETARIAN, VEGAN, DAIRY
}