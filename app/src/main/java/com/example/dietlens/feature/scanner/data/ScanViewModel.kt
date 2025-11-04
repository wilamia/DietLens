package com.example.dietlens.feature.scanner.data

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietlens.feature.allergy.AllergyPreferences
import com.example.dietlens.feature.home.LikedProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val repository: ProductRepository
) : ViewModel() {

    var product by mutableStateOf<Product?>(null)
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
    var allergyPrefs by mutableStateOf(AllergyPreferences())
        private set
    var detectedAllergens by mutableStateOf<List<AllergenKey>>(emptyList())
        private set

    var isTranslating by mutableStateOf(false)
        private set
    private val translator = ProductTranslator()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()
    private var currentBarcode: String? = null

    init {
        loadAllergyPreferences()
    }

    private fun loadAllergyPreferences() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val allergiesMap = snapshot?.get("allergies") as? Map<String, Boolean>
                if (allergiesMap != null) {
                    allergyPrefs = AllergyPreferences(
                        gluten = allergiesMap["gluten"] ?: false,
                        lactose = allergiesMap["lactose"] ?: false,
                        nuts = allergiesMap["nuts"] ?: false,
                        seafood = allergiesMap["seafood"] ?: false,
                        eggs = allergiesMap["eggs"] ?: false,
                        soy = allergiesMap["soy"] ?: false,
                        fruits = allergiesMap["fruits"] ?: false
                    )
                } else {
                    allergyPrefs = AllergyPreferences()
                }
            }
            .addOnFailureListener { e ->
                errorMessage = "Не удалось загрузить аллергены: ${e.message}"
            }
    }

    fun getProduct(barcode: String) {
        if (barcode.isBlank()) return

        clearState()
        isLoading = true
        currentBarcode = barcode

        viewModelScope.launch {
            repository.getProductDetails(barcode)
                .onSuccess { result ->
                    product = result.product
                    detectedAllergens = result.detectedAllergens

                    saveScanToHistory(result.product, result.detectedAllergens)
                    checkIfProductIsLiked(barcode)
                }
                .onFailure { exception ->
                    errorMessage = exception.message ?: "Произошла неизвестная ошибка"
                }

            isLoading = false
        }
    }

    private fun saveScanToHistory(product: Product, allergens: List<AllergenKey>) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.w("ScanViewModel", "User not logged in, cannot save history.")
            return
        }

        val historyItem = ScanHistoryItem(
            barcode = product.barcode ?: "",
            productName = product.product_name,
            imageUrl = product.image_front_url,
            detectedAllergens = allergens
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                firestore.collection("users")
                    .document(userId)
                    .collection("scanHistory")
                    .add(historyItem)
                    .await()
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Failed to save scan history", e)
            }
        }
    }

    private fun checkIfProductIsLiked(barcode: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val docRef = firestore.collection("users")
                    .document(userId)
                    .collection("likedProducts")
                    .document(barcode)

                val snapshot = docRef.get().await()
                _isLiked.value = snapshot.exists()
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Error checking liked status", e)
            }
        }
    }

    fun onLikeClicked() {
        val userId = auth.currentUser?.uid
        val barcode = currentBarcode
        val currentProduct = product

        if (userId == null || barcode.isNullOrBlank() || currentProduct == null) {
            Log.w("ScanViewModel", "Cannot process like click: invalid state.")
            return
        }

        val newLikedState = !_isLiked.value
        _isLiked.value = newLikedState

        val docRef = firestore.collection("users")
            .document(userId)
            .collection("likedProducts")
            .document(barcode)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (newLikedState) {
                    val likedProduct = LikedProduct(
                        barcode = barcode,
                        productName = currentProduct.product_name,
                        imageUrl = currentProduct.image_front_url
                    )
                    docRef.set(likedProduct).await()
                } else {
                    docRef.delete().await()
                }
            } catch (e: Exception) {
                Log.e("ScanViewModel", "Failed to update like status", e)
                _isLiked.value = !newLikedState
            }
        }
    }

    fun clearState() {
        product = null
        errorMessage = ""
        isLoading = false
        detectedAllergens = emptyList()
        _isLiked.value = false
        currentBarcode = null
    }
}