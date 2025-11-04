package com.example.dietlens.feature.restaurants

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.OnPrimary
import com.example.dietlens.theme.Selected
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RestaurantsMapScreenPerm(
    viewModel: RestaurantsMapViewModel = hiltViewModel()
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    // Use a state to track if location has been loaded
    var locationLoaded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // FusedLocationProviderClient is used to get the device's last known location
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    LaunchedEffect(locationPermissionState.status) {
        if (locationPermissionState.status.isGranted && !locationLoaded) {
            // Permission granted, now get the location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // ❗️ CHANGE: Call the initial load function
                    viewModel.loadInitialRestaurants(location.latitude, location.longitude)
                    locationLoaded = true
                }
            }
        } else {
            // Request permission if not granted
            locationPermissionState.launchPermissionRequest()
        }
    }

    when (locationPermissionState.status) {
        is PermissionStatus.Granted -> {
            // Pass the ViewModel state to the map screen
            val uiState by viewModel.uiState.collectAsState()
            RestaurantsMapScreen(
                uiState = uiState, onAllergyClicked = viewModel::toggleAllergy,
                onMapMoved = { newLatLng ->
                    viewModel.updateRestaurantsForLocation(newLatLng.latitude, newLatLng.longitude)
                },
                onRestaurantSelected = viewModel::onRestaurantSelected,
                onInfoWindowClosed = viewModel::onInfoWindowClosed
            )
        }

        is PermissionStatus.Denied -> {
            // UI to show when permission is denied
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Location permission is required to find nearby restaurants.",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantsMapScreen(
    uiState: RestaurantsMapUiState,
    onAllergyClicked: (Allergy) -> Unit,
    onMapMoved: (LatLng) -> Unit,
    onRestaurantSelected: (Restaurant) -> Unit,
    onInfoWindowClosed: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.userLocation, 14f)
    }
    val hue = remember(Buttons) {
        val hsv = FloatArray(3)
        AndroidColor.colorToHSV(Buttons.toArgb(), hsv)
        hsv[0]
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val newLocation = cameraPositionState.position.target
            onMapMoved(newLocation)
        }
    }

        Column(modifier = Modifier) {
            AllergyFilters(
                selectedAllergies = uiState.selectedAllergies,
                onAllergyClicked = onAllergyClicked
            )

            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { onInfoWindowClosed() }
                ) {
                    uiState.filteredRestaurants.forEach { restaurant ->

                        val markerState = rememberSaveable(
                            key = restaurant.id,
                            saver = MarkerState.Saver
                        ) {
                            MarkerState(position = restaurant.position)
                        }

                        Marker(
                            state = markerState,
                            title = restaurant.name,
                            onClick = {
                                onRestaurantSelected(restaurant)
                                false
                            },
                            icon = BitmapDescriptorFactory.defaultMarker(hue),
                        )
                    }
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }


                if (uiState.selectedRestaurant != null) {
                    RestaurantInfoCard(
                        modifier = Modifier
                            .padding(bottom = 100.dp)
                            .align(Alignment.BottomCenter),
                        restaurant = uiState.selectedRestaurant,
                        onClose = { onInfoWindowClosed() }
                    )
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantInfoCard(
    modifier: Modifier = Modifier,
    restaurant: Restaurant,
    onClose: () -> Unit
) {

    Card(
        modifier = modifier.fillMaxWidth(0.95f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = OnPrimary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(restaurant.photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = restaurant.name,
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.LightGray)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = restaurant.name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Close",
                                tint = Color.DarkGray
                            )
                        }
                    }
                    restaurant.address?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }

                    restaurant.rating?.let { rating ->
                        val total = restaurant.userRatingsTotal ?: 0
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "⭐ $rating ($total reviews)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        restaurant.isOpenNow?.let { isOpen ->
                            Text(
                                if (isOpen) "Open now" else "Closed",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isOpen) Color(0xFF388E3C) else Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        restaurant.priceLevel?.let { level ->
                            Text(
                                "$".repeat(level),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (restaurant.allergies.isNotEmpty()) {
                Text(
                    "⚠️ Possible allergens: ${restaurant.allergies.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllergyFilters(
    selectedAllergies: Set<Allergy>,
    onAllergyClicked: (Allergy) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pass the list directly to items()
        items(Allergy.entries) { allergy ->
            // This lambda will be executed for each allergy
            FilterChip(
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = Selected,
                    selectedLabelColor = Color.White,
                    disabledLabelColor = Selected,
                ),
                selected = allergy in selectedAllergies,
                onClick = { onAllergyClicked(allergy) },
                label = {
                    Text(
                        allergy.name,
                    )
                }
            )
        }
    }
}