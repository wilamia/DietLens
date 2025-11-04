package com.example.dietlens.feature.scanner.ui

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    private val scanner: BarcodeScanner,
    private val onBarcodeDetected: (String?) -> Unit
) : ImageAnalysis.Analyzer {

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = try {
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        } catch (e: Exception) {
            Log.e("BarcodeAnalyzer", "Failed to create InputImage", e)
            imageProxy.close()
            return
        }

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    val barcodeValue = barcodes.first().rawValue
                    if (barcodeValue != null) {
                        onBarcodeDetected(barcodeValue)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("BarcodeAnalyzer", "Barcode scanning failed", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}