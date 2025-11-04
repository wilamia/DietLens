import com.example.dietlens.feature.scanner.data.Product
import com.example.dietlens.feature.scanner.data.Translations
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.util.Locale

/**
 * Основная функция, которая принимает продукт и переводит все его текстовые поля.
 * ИСПОЛЬЗУЕТ СОВРЕМЕННЫЙ ПОДХОД С AWAIT()
 */
suspend fun translateProductDetails(product: Product, targetLanguage: String): Product {
    // Не переводим, если язык системы - английский (считаем его базовым)
    if (targetLanguage == TranslateLanguage.ENGLISH) {
        return product
    }

    val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(targetLanguage)
        .build()
    val translator = Translation.getClient(options)

    try {
        // 1. Ждем, пока модель скачается. await() превращает Task в suspend-функцию.
        translator.downloadModelIfNeeded().await()

        // 2. Запускаем параллельный перевод всех полей
        val translatedData = translateAllFields(product, translator)
        product.translations = translatedData

    } catch (e: Exception) {
        // Обработка ошибок (например, нет сети для скачивания модели)
        // Продукт вернется без перевода
        println("Translation failed: ${e.message}")
    } finally {
        // 3. Гарантированно закрываем транслятор, чтобы избежать утечек памяти
        translator.close()
    }

    return product
}

/**
 * Вспомогательная функция для параллельного перевода.
 * Обернута в coroutineScope, чтобы безопасно вызывать async.
 */
private suspend fun translateAllFields(product: Product, translator: Translator): Translations = coroutineScope {
    // `async` теперь вызывается на `CoroutineScope` (this), что является правильным.
    val productNameDeferred = async {
        runCatching { product.product_name?.let { translator.translateText(it) } }.getOrNull()
    }

    val ingredientsTextToTranslate = product.ingredientsTextEn ?: product.ingredients_text
    val ingredientsDeferred = async {
        runCatching { ingredientsTextToTranslate?.let { translator.translateText(it) } }.getOrNull()
    }

    val allergensDeferred = async {
        runCatching { product.allergens?.let { translateAllergens(it, translator) } }.getOrNull()
    }

    Translations(
        productName = productNameDeferred.await(),
        ingredients = ingredientsDeferred.await(),
        allergens = allergensDeferred.await()
    )
}

/**
 * Специальная функция для обработки строки аллергенов.
 */
private suspend fun translateAllergens(allergensString: String, translator: Translator): String {
    val allergens = allergensString.split(",")
        .map { it.trim().substringAfter(':') }
        .filter { it.isNotBlank() }

    return allergens.mapNotNull { allergen ->
        // Если перевод одного из аллергенов не удастся, он просто будет пропущен
        runCatching { translator.translateText(allergen) }.getOrNull()
    }.joinToString(", ")
}


/**
 * Упрощенное расширение для Translator, использующее await().
 */
private suspend fun Translator.translateText(text: String): String {
    // await() приостанавливает корутину и ждет результат, бросая исключение при ошибке.
    return this.translate(text).await()
}