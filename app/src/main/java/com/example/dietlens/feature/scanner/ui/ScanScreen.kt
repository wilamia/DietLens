package com.example.dietlens.feature.scanner.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.dietlens.R
import com.example.dietlens.feature.scanner.data.ScanViewModel
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary
import com.example.dietlens.theme.Selected

enum class ScanMode { INPUT, CAMERA }

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(viewModel: ScanViewModel = hiltViewModel()) {
    var selectedMode by remember { mutableStateOf(ScanMode.INPUT) }
    var barcode by remember { mutableStateOf("") }

    val isLiked by viewModel.isLiked.collectAsState()

    LaunchedEffect(selectedMode) {
        if (selectedMode == ScanMode.CAMERA) {
            viewModel.clearState()
            barcode = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .padding(horizontal = 16.dp)
            .background(OnPrimary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                onClick = { selectedMode = ScanMode.INPUT },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Buttons,
                    disabledActiveContentColor = OnPrimary,
                    activeBorderColor = Selected,
                    disabledActiveBorderColor = Buttons,
                    inactiveContentColor = Buttons,
                    activeContentColor = Color.White
                ),
                selected = selectedMode == ScanMode.INPUT
            ) {
                Text(stringResource(R.string.enter_barcode))
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                onClick = { selectedMode = ScanMode.CAMERA },
                selected = selectedMode == ScanMode.CAMERA,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = Buttons,
                    disabledActiveContentColor = OnPrimary,
                    activeBorderColor = Selected,
                    disabledActiveBorderColor = Buttons,
                    inactiveContentColor = Buttons,
                    activeContentColor = Color.White
                ),
            ) {
                Text(stringResource(R.string.choose_camera))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedContent(targetState = selectedMode, label = "scanModeAnimation") { mode ->
            when (mode) {
                ScanMode.INPUT -> InputView(
                    barcode = barcode,
                    onBarcodeChange = { barcode = it },
                    onCheckProduct = { viewModel.getProduct(barcode) },
                    isLoading = viewModel.isLoading
                )

                ScanMode.CAMERA -> CameraView(
                    onBarcodeScanned = { resultBarcode ->
                        barcode = resultBarcode
                        selectedMode = ScanMode.INPUT
                        viewModel.getProduct(resultBarcode)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            viewModel.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Text(
                    text = stringResource(R.string.searching_product),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            viewModel.isTranslating -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Text(
                    text = stringResource(R.string.translating_to_your_language),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            viewModel.errorMessage.isNotEmpty() -> {
                ErrorCard(message = viewModel.errorMessage)
            }

            viewModel.product != null -> {
                ProductDetails(
                    product = viewModel.product!!,
                    detectedAllergens = viewModel.detectedAllergens,
                    isLiked = isLiked,
                    modifier = Modifier,
                    onLikeClick = viewModel::onLikeClicked
                )
            }
        }
    }
}

@Composable
fun ErrorCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onErrorContainer,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}