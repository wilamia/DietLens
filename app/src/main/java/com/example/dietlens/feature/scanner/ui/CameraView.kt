package com.example.dietlens.feature.scanner.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraView(onBarcodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        CameraPreview(onBarcodeScanned)
    } else {
        PermissionRequestScreen {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(OnPrimary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Camera Icon",
            modifier = Modifier.size(64.dp),
            tint = Buttons
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Требуется доступ к камере",
            style = MaterialTheme.typography.titleLarge,
            color = Buttons,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Чтобы сканировать штрих-коды, приложению нужен доступ к вашей камере.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Buttons
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("Предоставить доступ", color = Buttons)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CameraPreview(onBarcodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var isScanningEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .background(OnPrimary)
            .clip(MaterialTheme.shapes.large)
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = Executors.newSingleThreadExecutor()
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor, BarcodeAnalyzer(
                                scanner = BarcodeScanning.getClient(
                                    BarcodeScannerOptions.Builder()
                                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                                        .build()
                                ),
                                onBarcodeDetected = { barcode ->
                                    if (barcode != null && isScanningEnabled) {
                                        isScanningEnabled = false
                                        onBarcodeScanned(barcode)
                                    }
                                }
                            ))
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalyzer)
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.matchParentSize()
        )
        ScannerOverlay(modifier = Modifier.matchParentSize())
    }
}


@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    frameColor: Color = Buttons,
    scrimColor: Color = Color.Black.copy(alpha = 0.55f),
    cornerRadiusDp: Float = 16f,
    strokeWidthDp: Float = 2f,
    showLaser: Boolean = true,
    laserColor: Color = Buttons.copy(alpha = 0.85f),
) {
    val density = LocalDensity.current
    val strokeWidth = with(density) { (strokeWidthDp.dp).toPx() }
    val cornerPx = with(density) { (cornerRadiusDp.dp).toPx() }

    // анимация «лазера»
    val transition = rememberInfiniteTransition(label = "scan")
    val laserY by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Canvas(modifier = modifier) {
        val rectWidth = size.width * 0.8f
        val rectHeight = size.height * 0.42f
        val rectLeft = (size.width - rectWidth) / 2f
        val rectTop = (size.height - rectHeight) / 2f
        val rect = androidx.compose.ui.geometry.Rect(
            left = rectLeft, top = rectTop,
            right = rectLeft + rectWidth, bottom = rectTop + rectHeight
        )

        // затемняем фон
        drawRect(scrimColor)

        // вырезаем окно сканера
        drawRoundRect(
            color = Color.Transparent,
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = CornerRadius(cornerPx, cornerPx),
            blendMode = BlendMode.Clear
        )

        // рамка
        drawRoundRect(
            color = frameColor,
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = CornerRadius(cornerPx, cornerPx),
            style = Stroke(width = strokeWidth)
        )

        // лазерная линия (по желанию)
        if (showLaser) {
            val y = rect.top + rect.height * laserY
            drawLine(
                color = laserColor,
                start = Offset(rect.left + 8f, y),
                end   = Offset(rect.right - 8f, y),
                strokeWidth = strokeWidth * 1.6f,
                cap = Stroke.DefaultCap
            )
        }
    }
}
