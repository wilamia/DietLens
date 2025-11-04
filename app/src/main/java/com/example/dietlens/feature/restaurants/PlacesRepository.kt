package com.example.dietlens.feature.restaurants

import android.util.Log
import com.example.dietlens.BuildConfig
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject


class PlacesRepository @Inject constructor(
    private val placesApiService: PlacesApiService
) {

    suspend fun getNearbyRestaurants(latitude: Double, longitude: Double): List<Restaurant> {
        val locationString = "$latitude,$longitude"
        return try {
            val response = placesApiService.nearbyRestaurants(
                location = locationString,
                apiKey = BuildConfig.MAPS_API_KEY
            )

            Log.d(
                "PlacesRepo",
                "NearbySearch status=${response.status}, error=${response.error_message}, results=${response.results.size}"
            )

            if (response.status != null && response.status != "OK" && response.status != "ZERO_RESULTS") {
                Log.e(
                    "PlacesRepo",
                    "Places API error: ${response.status} - ${response.error_message}"
                )
                return emptyList()
            }

            response.results.mapNotNull { place ->
                val photoReference = place.photos?.firstOrNull()?.photo_reference
                val fullPhotoUrl = if (photoReference != null) {
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoReference&key=${BuildConfig.MAPS_API_KEY}"
                } else {
                    null
                }

                // 🔥 ГЛАВНОЕ ИЗМЕНЕНИЕ:
                // Вызываем нашу новую, умную функцию детектора аллергенов
                val detectedAllergies = detectAllergies(
                    name = place.name,
                    types = place.types
                )

                Restaurant(
                    id = place.place_id,
                    name = place.name,
                    position = LatLng(place.geometry.location.lat, place.geometry.location.lng),

                    // 👇 Подставляем найденные аллергены
                    allergies = detectedAllergies,

                    photoUrl = fullPhotoUrl,
                    websiteUrl = place.website,
                    address = place.vicinity,
                    rating = place.rating,
                    userRatingsTotal = place.user_ratings_total,
                    isOpenNow = place.opening_hours?.open_now,
                    priceLevel = place.price_level,
                    phoneNumber = place.international_phone_number,
                    businessStatus = place.business_status
                )
            }
        } catch (e: Exception) {
            Log.e("PlacesRepo", "Exception loading restaurants", e)
            emptyList()
        }
    }

    private fun detectAllergies(name: String?, types: List<String>): List<Allergy> {
        val detected = mutableSetOf<Allergy>()
        if (name == null) return emptyList()

        val lowerName = name.lowercase()
        val lowerTypes = types.map { it.lowercase() }

        val negativeKeywords = listOf(
            "free",
            "bez",
            "sin",
            "senza",
            "ohne",
            "sans"
        )

        val glutenKeywords = listOf(
            "gluten", "wheat", "bread", "pasta", "pizza", "pastry", "bakery", "noodle",
            "глютен", "пшениц", "хлеб", "паста", "пицца", "выпечка", "лапша", "булочная"
        )

        if (glutenKeywords.any { lowerName.contains(it) }) {
            if (negativeKeywords.none { lowerName.contains(it) }) {
                detected.add(Allergy.GLUTEN)
            }
        }

        val nutsKeywords = listOf(
            "nut", "peanut", "almond", "walnut", "cashew", "pistachio",
            "орех", "арахис", "миндаль", "фисташк"
        )

        if (nutsKeywords.any { lowerName.contains(it) } &&
            negativeKeywords.none { lowerName.contains(it) }) {
            detected.add(Allergy.NUTS)
        }

        val dairyKeywords = listOf(
            "milk", "cheese", "dairy", "ice cream", "yogurt", "cream",
            "молоко", "сыр", "мороженое", "йогурт", "сливки"
        )

        if (dairyKeywords.any { lowerName.contains(it) } &&
            negativeKeywords.none { lowerName.contains(it) }) {
            detected.add(Allergy.DAIRY)
        }


        val isFreeFromShopByName = negativeKeywords.any { lowerName.contains(it) }

        if (lowerTypes.any { it in listOf("bakery", "pastry_shop", "pizza_place", "sandwich_shop", "pasta_shop", "noodle_shop") }) {
            if (!isFreeFromShopByName) {
                detected.add(Allergy.GLUTEN)
            }
        }

        if (lowerTypes.contains("vegan_restaurant")) {
            detected.add(Allergy.VEGAN)
        }
        if (lowerTypes.contains("vegetarian_restaurant")) {
            detected.add(Allergy.VEGETARIAN)
        }

        return detected.toList()
    }
    }