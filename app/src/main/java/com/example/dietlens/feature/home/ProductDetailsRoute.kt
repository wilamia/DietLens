package com.example.dietlens.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dietlens.feature.scanner.ui.ProductDetails

@Composable
fun ProductDetailsRoute(
    viewModel: ProductDetailsViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state by viewModel.state.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()

    when (val currentState = state) {
        is ProductDetailsViewModel.ProductDetailsState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ProductDetailsViewModel.ProductDetailsState.Success -> {
            val product = currentState.product
            val detectedAllergens = currentState.detectedAllergens

            ProductDetails(
                modifier = modifier.fillMaxSize(),
                product = product,
                detectedAllergens = detectedAllergens,
                onLikeClick = { viewModel.onLikeClicked() },
                isLiked = isLiked
            )
        }

        is ProductDetailsViewModel.ProductDetailsState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(), contentAlignment = Alignment.Center
            ) {
                Text(currentState.message)
            }
        }
    }
}
