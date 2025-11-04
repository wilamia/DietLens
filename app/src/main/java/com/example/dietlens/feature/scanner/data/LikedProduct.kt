package com.example.dietlens.feature.scanner.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LikedProduct(
    val barcode: String = "",
    val productName: String? = null,
    val imageUrl: String? = null,
    @ServerTimestamp
    val likedAt: Date? = null
)