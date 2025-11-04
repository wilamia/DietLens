package com.example.dietlens.feature.profile

data class FavoriteProduct(
    val productId: String,
    val name: String,
    val imageUrl: String?,
    val brand: String? = null,
    val liked: Boolean = true
)
