// In file: feature/scanner/data/ProductRepository.kt
package com.example.dietlens.feature.scanner.data

import android.util.Log
import com.example.dietlens.feature.allergy.AllergyPreferences
// ✅ Убедитесь, что импортируется правильный enum
import com.example.dietlens.feature.home.ProductCategoryEnum
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class ProductScanResult(
    val product: Product,
    val detectedAllergens: List<AllergenKey>
)

@Singleton
class ProductRepository @Inject constructor(
    private val api: OpenFoodFactsApi,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val translator = ProductTranslator()

    suspend fun getProducts(category: ProductCategoryEnum, page: Int): List<Product> =
        withContext(Dispatchers.IO) {
            try {
                val response = when (category) {
                    ProductCategoryEnum.ALL -> api.searchAllProducts(page = page)
                    else -> api.getProductsByCategory(category.apiTag, page = page)
                }

                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    val nameRegex = ".*[\\p{IsLatin}\\p{IsCyrillic}].*".toRegex()
                    products.filter { product ->
                        !product.product_name.isNullOrBlank() && nameRegex.matches(product.product_name)
                    }
                } else {
                    Log.e("ProductRepository", "API Error: ${response.code()} ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "Failed to get products list: ${e.message}", e)
                emptyList()
            }
        }

    suspend fun getProductDetails(barcode: String): Result<ProductScanResult> {
        return withContext(Dispatchers.IO) {
            try {
                coroutineScope {
                    val productDeferred = async { fetchProductFromApi(barcode) }
                    val prefsDeferred = async { fetchUserAllergyPreferences() }

                    val product = productDeferred.await()
                    val allergyPrefs = prefsDeferred.await()

                    if (product == null) {
                        return@coroutineScope Result.failure(Exception("Продукт с таким штрихкодом не найден"))
                    }

                    val translatedProduct = translator.translateProduct(product)
                    val allergens = AllergyChecker.checkAllergens(translatedProduct, allergyPrefs)

                    // ⬇️ тут уже список ключей, ок
                    Result.success(ProductScanResult(translatedProduct, allergens))
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "Failed to get product details", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun fetchProductFromApi(barcode: String): Product? {
        return try {
            val response = api.getProductByBarcode(barcode)
            if (response.isSuccessful) {
                val productResponse = response.body()
                if (productResponse != null && productResponse.status == 1) {
                    productResponse.product
                } else {
                    null
                }
            } else {
                Log.e("ProductRepository", "API Error: ${response.code()} ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to fetch from API: ${e.message}", e)
            null
        }
    }

    private suspend fun fetchUserAllergyPreferences(): AllergyPreferences {
        val userId = auth.currentUser?.uid ?: return AllergyPreferences()
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            val allergiesMap = snapshot.get("allergies") as? Map<String, Boolean>
            if (allergiesMap != null) {
                AllergyPreferences(
                    gluten = allergiesMap["gluten"] ?: false,
                    lactose = allergiesMap["lactose"] ?: false,
                    nuts = allergiesMap["nuts"] ?: false,
                    seafood = allergiesMap["seafood"] ?: false,
                    eggs = allergiesMap["eggs"] ?: false,
                    soy = allergiesMap["soy"] ?: false,
                    fruits = allergiesMap["fruits"] ?: false
                )
            } else {
                AllergyPreferences()
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Failed to load allergy preferences", e)
            AllergyPreferences()
        }
    }
}