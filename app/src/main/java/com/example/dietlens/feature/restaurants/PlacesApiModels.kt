package com.example.dietlens.feature.restaurants

data class NearbySearchResponse(
    val results: List<PlaceResult> = emptyList(),
    val status: String? = null,
    val error_message: String? = null
)


data class PlaceResult(
    val place_id: String,
    val name: String,
    val geometry: Geometry,
    val types: List<String>,
    val photos: List<Photo>?,

    // --- НОВЫЕ ПОЛЯ ---
    val business_status: String?,     // "OPERATIONAL"
    val opening_hours: OpeningHours?,  // { "open_now": false }
    val plus_code: PlusCode?,          // { "compound_code": "...", "global_code": "..." }
    val price_level: Int?,             // 2
    val rating: Double?,               // 3.5
    val user_ratings_total: Int?,    // 1284
    val vicinity: String?,             // "Rakowiecka 41, Warszawa" (Адрес)
    val international_phone_number: String?, // "+48 22 625 76 27"
    val icon: String?,
    val website: String?,
    val icon_background_color: String?, // "#FF9E67"
    val icon_mask_base_uri: String?    // URL
)
data class Photo(
    val photo_reference: String,
    // --- НОВЫЕ ПОЛЯ ---
    val height: Int?,
    val width: Int?,
    val html_attributions: List<String>?
)

// Этот класс без изменений
data class Geometry(
    val location: Location
)

// Этот класс без изменений
data class Location(
    val lat: Double,
    val lng: Double
)

//
// 🔥 НОВЫЕ КЛАССЫ ДЛЯ ВЛОЖЕННЫХ ДАННЫХ
//
data class OpeningHours(
    val open_now: Boolean?
)

data class PlusCode(
    val compound_code: String?,
    val global_code: String?
)