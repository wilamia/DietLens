package com.example.dietlens.feature.scanner.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val status: Int,
    val product: Product?
)
data class Translations(
    val productName: String?,
    val ingredients: String?,
    val allergens: String?
)

data class Product(
    @SerializedName("code") val barcode: String?,
    val product_name: String?,
    val brands: String?,
    val ingredients_text: String?,
    val allergens: String?,
    val nutriments: Nutriments?,
    val product_quantity: Float?,
    val product_quantity_unit: String?,
    val image_front_url: String?,

    @SerializedName("ingredients_text_en")
    val ingredientsTextEn: String?,
    @SerializedName("traces") val traces: String?,
    @SerializedName("nutriscore_data")
    val nutriscoreData: JsonElement?,

    var translations: Translations? = null
) {
    fun getLatestNutriscore(): NutriscoreYearData? {
        if (nutriscoreData?.isJsonObject != true) return null

        val jsonObject = nutriscoreData.asJsonObject
        val latestYear = jsonObject.keySet().maxOrNull() ?: return null

        return try {
            Gson().fromJson(jsonObject[latestYear], NutriscoreYearData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

data class Nutriments(
    @SerializedName("energy-kcal") val energyKcal: Double?,
    val proteins: Double?,
    val fat: Double?,
    @SerializedName("saturated-fat") val saturatedFat: Double?,
    val carbohydrates: Double?,
    val sugars: Double?,
    val salt: Double?,
    val alcohol: Double?,
    @SerializedName("alcohol_unit") val alcoholUnit: String?,
    val fiber: Double?,
    @SerializedName("vitamin-e") val vitaminE: Double?
)

data class NutriscoreYearData(
    @SerializedName("category_available") val categoryAvailable: Int?,
    val data: NutriscoreNutrients?,
    val grade: String?,
    @SerializedName("nutrients_available") val nutrientsAvailable: Int?,
    val nutriscore_applicable: Int?,
    val nutriscore_computed: Int?
)

data class NutriscoreNutrients(
    val energy: Double?,
    val fiber: Double?,
    val fruits_vegetables_legumes: Int?,
    val is_beverage: Int?,
    val is_cheese: Int?,
    val is_fat_oil_nuts_seeds: Int?,
    val is_red_meat_product: Int?,
    val is_water: Int?,
    val non_nutritive_sweeteners: Int?,
    val proteins: Double?,
    val salt: Double?,
    val saturated_fat: Double?,
    val sugars: Double?
)

suspend fun fetchProduct(api: OpenFoodFactsApi, barcode: String): Product? {
    return try {
        val response = api.getProductByBarcode(barcode)
        if (response.isSuccessful) {
            // Успешный ответ (код 2xx)
            response.body()?.takeIf { it.status == 1 }?.product
        } else {
            // Сервер ответил ошибкой (коды 4xx, 5xx)
            Log.e("fetchProduct", "Ошибка API: Код=${response.code()}, Сообщение=${response.message()}")
            null
        }
    } catch (e: Exception) {
        // Ошибка сети или парсинга JSON
        Log.e("fetchProduct", "Не удалось выполнить запрос: ${e.message}", e)
        null
    }
}
data class UiProduct(
    val barcode: String,
    val name: String,
    val brand: String,
    val imageUrl: String
)
data class CategoryResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("page_count") val pageCount: Int,
    @SerializedName("products") val products: List<Product> // This is the list your code is looking for
)
data class ProductCategory(
    val name: String,
    val products: List<UiProduct>
)
fun Product.toUiProduct(): UiProduct {
    return UiProduct(
        barcode = this.barcode ?: "",
        name = this.product_name ?: "Unknown Product",
        brand = this.brands ?: "Unknown Brand",
        imageUrl = this.image_front_url ?: ""
    )
}