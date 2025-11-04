package com.example.dietlens.feature.scanner.data

import com.example.dietlens.feature.allergy.AllergyPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AllergenKey {
    GLUTEN, LACTOSE, NUTS, SOY, EGGS, SEAFOOD, FRUITS
}

object AllergyChecker {

    suspend fun checkAllergens(
        product: Product?,
        prefs: AllergyPreferences
    ): List<AllergenKey> = withContext(Dispatchers.Default) {
        if (product == null) return@withContext emptyList()

        val translatedAllergens = product.translations?.allergens?.split(",") ?: emptyList()

        val allAllergenTags = translatedAllergens
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .toSet()

        if (allAllergenTags.isEmpty()) return@withContext emptyList()

        val detected = mutableListOf<AllergenKey>()
        val joinedTags = allAllergenTags.joinToString(",")

        // тут только логика, никаких языков
        if (prefs.gluten && Regex("gluten").containsMatchIn(joinedTags)) {
            detected.add(AllergenKey.GLUTEN)
        }

        if (prefs.lactose && Regex("milk|lactose|whey|butter|cream|casein").containsMatchIn(joinedTags)) {
            detected.add(AllergenKey.LACTOSE)
        }

        if (prefs.nuts && Regex("nut|almond|hazelnut|cashew|walnut|pecan|pistachio|brazil-nut|tree-nut|peanut")
                .containsMatchIn(joinedTags)
        ) {
            detected.add(AllergenKey.NUTS)
        }

        if (prefs.soy && Regex("soy|soya|soybean").containsMatchIn(joinedTags)) {
            detected.add(AllergenKey.SOY)
        }

        if (prefs.eggs && Regex("egg|ovum|albumin").containsMatchIn(joinedTags)) {
            detected.add(AllergenKey.EGGS)
        }

        if (prefs.seafood && Regex("fish|crustacean|mollusc|shrimp|prawn|crab|lobster|clam|oyster|squid|octopus")
                .containsMatchIn(joinedTags)
        ) {
            detected.add(AllergenKey.SEAFOOD)
        }

        if (prefs.fruits && Regex("apple|banana|orange|kiwi|strawberry|peach").containsMatchIn(joinedTags)) {
            detected.add(AllergenKey.FRUITS)
        }

        return@withContext detected.distinct()
    }
}
