package com.example.dietlens.feature.profile

data class ScanHistoryEntry(
    val docId: String,
    val barcode: String,
    val name: String,
    val imageUrl: String?,
    val detectedAllergens: List<String> = emptyList(),
    val scannedAt: com.google.firebase.Timestamp? = null
)