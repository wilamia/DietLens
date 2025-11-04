package com.example.dietlens.feature.home

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietlens.feature.scanner.data.AllergenKey
import com.example.dietlens.feature.scanner.data.Product
import com.example.dietlens.feature.scanner.data.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

data class LikedProduct(
    val barcode: String = "",
    val productName: String? = null,
    val imageUrl: String? = null,
    @ServerTimestamp
    val likedAt: Date? = null
)

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: ProductRepository,
    savedStateHandle: SavedStateHandle,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    sealed class ProductDetailsState {
        object Loading : ProductDetailsState()
        data class Success(val product: Product, val detectedAllergens: List<AllergenKey>) : ProductDetailsState()
        data class Error(val message: String) : ProductDetailsState()
    }

    private val _state = MutableStateFlow<ProductDetailsState>(ProductDetailsState.Loading)
    val state: StateFlow<ProductDetailsState> = _state.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked.asStateFlow()

    private val _isLikeStatusLoading = MutableStateFlow(true)
    val isLikeStatusLoading: StateFlow<Boolean> = _isLikeStatusLoading.asStateFlow()

    private var currentBarcode: String? = null

    init {
        val barcode: String? = savedStateHandle["barcode"]
        currentBarcode = barcode
        if (!barcode.isNullOrBlank()) {
            fetchProductDetails(barcode)
        } else {
            _state.value = ProductDetailsState.Error("Штрихкод не был предоставлен")
            _isLikeStatusLoading.value = false // Ошибка, загрузку можно остановить
        }
    }

    private fun fetchProductDetails(barcode: String) {
        viewModelScope.launch {
            _state.value = ProductDetailsState.Loading
            _isLikeStatusLoading.value = true // Начинаем загрузку статуса
            repository.getProductDetails(barcode)
                .onSuccess { result ->
                    _state.value = ProductDetailsState.Success(
                        product = result.product,
                        detectedAllergens = result.detectedAllergens
                    )
                    // Проверяем лайк (теперь эта функция сработает)
                    checkIfProductIsLiked(barcode)
                }
                .onFailure { exception ->
                    _state.value = ProductDetailsState.Error(
                        exception.message ?: "Произошла неизвестная ошибка"
                    )
                    _isLikeStatusLoading.value = false // Ошибка, загрузку останавливаем
                }
        }
    }

    //
    // --- ИСПРАВЛЕННАЯ ФУНКЦИЯ ---
    //
    private fun checkIfProductIsLiked(barcode: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _isLikeStatusLoading.value = false // Пользователь не вошел, грузить нечего
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            //
            // ОШИБКА БЫЛА ЗДЕСЬ: Мы УБРАЛИ неправильную проверку if (!_isLikeStatusLoading.value)
            //
            try {
                // 'true' уже установлено в fetchProductDetails

                val docRef = firestore.collection("users")
                    .document(userId)
                    .collection("likedProducts")
                    .document(barcode)

                val snapshot = docRef.get().await()
                _isLiked.value = snapshot.exists() // true, если документ существует

            } catch (e: Exception) {
                Log.e("ProductDetailsVM", "Error checking liked status", e)
                _isLiked.value = false // Безопасное значение по умолчанию
            } finally {
                // ГАРАНТИРУЕМ, что загрузка завершится,
                // даже если была ошибка в 'try'
                _isLikeStatusLoading.value = false
            }
        }
    }


    fun onLikeClicked() {
        if (_isLikeStatusLoading.value) return

        val currentState = _state.value
        val userId = auth.currentUser?.uid
        val barcode = currentBarcode
        if (currentState !is ProductDetailsState.Success || userId == null || barcode.isNullOrBlank()) return

        _isLikeStatusLoading.value = true
        val newLikedState = !_isLiked.value
        _isLiked.value = newLikedState

        val product = currentState.product
        val docRef = firestore.collection("users")
            .document(userId)
            .collection("likedProducts")
            .document(barcode)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (newLikedState) {
                    // ⬇️ ПИШЕМ КЛЮЧИ, КОТОРЫЕ ПОТОМ ЧИТАЕШЬ В FavoritesSheet
                    val data = mapOf(
                        "name" to (product.product_name ?: "Без названия"),
                        "imageUrl" to product.image_front_url,
                        "brand" to product.brands,
                        "likedAt" to FieldValue.serverTimestamp()
                    )
                    docRef.set(data, SetOptions.merge()).await()
                } else {
                    docRef.delete().await()
                }
            } catch (e: Exception) {
                Log.e("ProductDetailsVM", "Failed to update like status, rolling back.", e)
                _isLiked.value = !newLikedState
            } finally {
                _isLikeStatusLoading.value = false
            }
        }
    }
}