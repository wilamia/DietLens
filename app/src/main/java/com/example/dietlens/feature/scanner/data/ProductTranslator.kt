package com.example.dietlens.feature.scanner.data

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import java.util.*

class ProductTranslator {

    private val translatorCache = mutableMapOf<Pair<String, String>, Translator>()

    suspend fun translateProduct(product: Product): Product {
        val localLangTag = Locale.getDefault().language
        val localLanguage = TranslateLanguage.fromLanguageTag(localLangTag) ?: TranslateLanguage.ENGLISH
        val commonLanguage = TranslateLanguage.ENGLISH

        try {
            // 1. Переводим Ингредиенты на ЛОКАЛЬНЫЙ язык (для UI)
            val translatedIngredients = translateField(
                product.ingredientsTextEn ?: product.ingredients_text,
                localLanguage // Цель: ru, pl, de...
            )

            //
            // --- ИЗМЕНЕНИЕ ЗДЕСЬ ---
            //
            // 2. Собираем ОБА списка: и аллергены, и следы
            val allergensList = product.allergens?.split(",")?.map { it.trim() } ?: emptyList()
            val tracesList = product.traces?.split(",")?.map { it.trim() } ?: emptyList() // <-- НОВАЯ СТРОКА

            // 3. Объединяем их в один список
            val allPotentialAllergens = (allergensList + tracesList)
                .filter { it.isNotBlank() } // Убираем пустые
                .distinct() // Убираем дубликаты (если "milk" есть в обоих)

            // 4. Переводим ОБЪЕДИНЕННЫЙ список на АНГЛИЙСКИЙ (для Логики)
            val translatedAllergensList = allPotentialAllergens.map { allergen ->
                translateField(
                    allergen,
                    commonLanguage // Цель: en
                ) ?: allergen
            }
            // --- КОНЕЦ ИЗМЕНЕНИЙ ---

            // 5. Сохраняем результаты
            val translatedAllergens = translatedAllergensList.joinToString(", ")

            product.translations = Translations(
                productName = product.product_name,
                ingredients = translatedIngredients, // -> "Вода, сахар, молоко..."
                allergens = translatedAllergens      // -> "milk, nuts, soy"
            )

        } catch (e: Exception) {
            Log.e("ProductTranslator", "Translation failed", e)
        } finally {
            translatorCache.values.forEach { it.close() }
        }

        return product
    }

    /**
     * Вспомогательная функция, которая переводит текст на УКАЗАННЫЙ язык
     */
    private suspend fun translateField(text: String?, targetLanguage: String): String? {
        if (text.isNullOrBlank()) return text

        val sourceLanguage = detectSupportedLanguage(text)
        val cleanText = text.replace(Regex("^[a-z]{2}:", RegexOption.IGNORE_CASE), "").trim()

        if (sourceLanguage == targetLanguage) {
            return cleanText
        }

        val key = sourceLanguage to targetLanguage
        val translator = translatorCache.getOrPut(key) {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguage)
                .setTargetLanguage(targetLanguage)
                .build()
            Translation.getClient(options)
        }

        downloadModelIfNeeded(translator)
        val translated = translateText(translator, text)
        return translated?.replace(Regex("^[a-z]{2}:", RegexOption.IGNORE_CASE), "")?.trim()
    }


    private suspend fun detectSupportedLanguage(text: String?): String {
        if (text.isNullOrBlank()) return TranslateLanguage.ENGLISH

        val langPrefix = text.substringBefore(":").lowercase()
        if (langPrefix.length == 2 && TranslateLanguage.fromLanguageTag(langPrefix) != null) {
            return langPrefix
        }

        val cleaned = text.lowercase().replace(Regex("[^\\p{L}\\s]"), " ")
        return try {
            val languageIdentifier = LanguageIdentification.getClient()
            val detectedTag = languageIdentifier.identifyLanguage(cleaned).await()
            val mlKitLanguage = TranslateLanguage.fromLanguageTag(detectedTag)
            mlKitLanguage ?: TranslateLanguage.ENGLISH
        } catch (e: Exception) {
            TranslateLanguage.ENGLISH
        }
    }

    private suspend fun translateText(translator: Translator, text: String?): String? {
        if (text.isNullOrBlank()) return text
        return try {
            translator.translate(text).await()
        } catch (e: Exception) {
            text
        }
    }

    private suspend fun downloadModelIfNeeded(translator: Translator) {
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions).await()
    }
}