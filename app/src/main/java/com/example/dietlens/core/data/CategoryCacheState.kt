package com.example.dietlens.core.data

import com.example.dietlens.feature.home.ProductCategoryEnum
import com.example.dietlens.feature.scanner.data.Product
import javax.inject.Inject
import javax.inject.Singleton

// Этот data class можно вынести из VM и сделать публичным
data class CategoryCacheState(
    val products: List<Product> = emptyList(),
    val currentPage: Int = 1,
    val endReached: Boolean = false
)

// ✅ ШАГ 1: Создаем класс
@Singleton // 👈 Hilt будет хранить один экземпляр этого класса ВЕЧНО
class InMemoryProductCache @Inject constructor() {

    // ✅ ШАГ 2: Переносим кэш из ViewModel сюда
    private val categoryCache = mutableMapOf<ProductCategoryEnum, CategoryCacheState>()

    // ✅ ШАГ 3: Создаем методы для управления кэшем

    fun getState(category: ProductCategoryEnum): CategoryCacheState {
        return categoryCache[category] ?: CategoryCacheState()
    }

    fun updateState(category: ProductCategoryEnum, newState: CategoryCacheState) {
        categoryCache[category] = newState
    }

    fun clearAll() {
        categoryCache.clear()
    }
}