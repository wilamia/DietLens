package com.example.dietlens.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietlens.core.data.InMemoryProductCache
import com.example.dietlens.feature.scanner.data.Product
import com.example.dietlens.feature.scanner.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repo: ProductRepository,
                                        private val cache: InMemoryProductCache) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedCategory = MutableStateFlow(ProductCategoryEnum.ALL)
    val selectedCategory: StateFlow<ProductCategoryEnum> = _selectedCategory

    val allCategories: List<ProductCategoryEnum> = ProductCategoryEnum.values().toList()


    private var fetchJob: Job? = null

    // ❌ УДАЛИТЕ ЭТИ ГЛОБАЛЬНЫЕ ПЕРЕМЕННЫЕ
    // private var currentPage = 1
    // private var endReached = false

    // ✅ ЗАМЕНИТЕ ЭТУ ФУНКЦИЮ
    fun loadCategory(category: ProductCategoryEnum) {
        if (category == _selectedCategory.value) return
        fetchJob?.cancel()
        _selectedCategory.value = category

        // ✅ ШАG 2: Используйте 'cache.getState()'
        val cachedState = cache.getState(category)

        if (cachedState != null) {
            // ✅ Есть в кэше - просто восстанавливаем состояние
            _products.value = cachedState.products
            _isLoading.value = false
            _errorMessage.value = null

            // Если вдруг в кэше пустой список, но мы знаем, что это не конец
            // (например, была ошибка), попробуем загрузить первую страницу
            if (cachedState.products.isEmpty() && !cachedState.endReached) {
                loadNextPage()
            }
        } else {
            // ✅ Нет в кэше - сбрасываем UI и грузим первую страницу
            _products.value = emptyList()
            _errorMessage.value = null
            loadNextPage() // Он сам поймет, что нужно грузить 1-ю страницу
        }
    }

    // ✅ ЗАМЕНИТЕ ЭТУ ФУНКЦИЮ
    fun loadNextPage() {
        val currentCategory = _selectedCategory.value
        // ✅ Получаем состояние ИЗ КЭША
        val currentState = cache.getState(currentCategory)

        if (_isLoading.value || currentState.endReached || fetchJob?.isActive == true) return

        fetchJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val newItems = repo.getProducts(currentCategory, currentState.currentPage)
                val newProducts = currentState.products + newItems
                val newEndReached = newItems.isEmpty()

                // ✅ Обновляем состояние В КЭШЕ
                cache.updateState(currentCategory, currentState.copy(
                    products = newProducts,
                    currentPage = currentState.currentPage + 1,
                    endReached = newEndReached
                ))

                _products.value = newProducts

            } catch (e: Exception) {
                Log.e("MainViewModel", "Failed to load products", e)
                _errorMessage.value = "Не удалось загрузить продукты"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getProductByBarcode(barcode: String): Product? {
        // Эта функция, как и раньше, ищет только в текущем видимом списке
        return _products.value.find { it.barcode == barcode }
    }

    // ❌ УДАЛИТЕ ФУНКЦИЮ resetAndLoad()
    // Она больше не нужна, ее логика теперь в loadCategory и loadNextPage

    init {
        // Загружаем первую страницу для категории по умолчанию (ALL)
        loadNextPage()
    }
}