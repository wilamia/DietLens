package com.example.dietlens.feature.scanner.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ScanHistoryItem(
    val barcode: String = "",
    val productName: String? = null,
    val imageUrl: String? = null,
    @ServerTimestamp
    val scannedAt: Date? = null,
    val detectedAllergens: List<AllergenKey> = emptyList()
)