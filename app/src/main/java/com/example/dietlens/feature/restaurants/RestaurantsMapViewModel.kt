package com.example.dietlens.feature.restaurants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RestaurantsMapUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val userLocation: LatLng = LatLng(52.2297, 21.0122), // Default to Warsaw
    val allRestaurants: List<Restaurant> = emptyList(),
    val filteredRestaurants: List<Restaurant> = emptyList(),
    val selectedAllergies: Set<Allergy> = emptySet(),
    val lastLoadedLocation: LatLng? = null,
    val selectedRestaurant: Restaurant? = null
)

@HiltViewModel
class RestaurantsMapViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RestaurantsMapUiState())
    val uiState: StateFlow<RestaurantsMapUiState> = _uiState.asStateFlow()

    fun loadInitialRestaurants(latitude: Double, longitude: Double) {
        if (_uiState.value.allRestaurants.isNotEmpty()) return
        updateRestaurantsForLocation(latitude, longitude)
    }

    fun updateRestaurantsForLocation(latitude: Double, longitude: Double) {
        // Логика для предотвращения частых запросов остается прежней...
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val newRestaurants = placesRepository.getNearbyRestaurants(latitude, longitude)
                _uiState.update { currentState ->
                    val updatedList = (currentState.allRestaurants + newRestaurants).distinctBy { it.id }
                    // ✅ ПРИМЕНЯЕМ ФИЛЬТРЫ СРАЗУ ЗДЕСЬ
                    val filteredList = applyFilters(updatedList, currentState.selectedAllergies)
                    currentState.copy(
                        isLoading = false,
                        allRestaurants = updatedList,
                        filteredRestaurants = filteredList, // Обновляем отфильтрованный список
                        lastLoadedLocation = LatLng(latitude, longitude),
                        userLocation = if (currentState.lastLoadedLocation == null) LatLng(latitude, longitude) else currentState.userLocation
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load restaurants: ${e.message}") }
            }
        }
    }

    fun toggleAllergy(allergy: Allergy) {
        _uiState.update { currentState ->
            val newSelection = currentState.selectedAllergies.toMutableSet()
            if (newSelection.contains(allergy)) {
                newSelection.remove(allergy)
            } else {
                newSelection.add(allergy)
            }

            // ✅ ПРИМЕНЯЕМ ФИЛЬТРЫ ВНУТРИ ОДНОГО UPDATE
            val filteredList = applyFilters(currentState.allRestaurants, newSelection)

            currentState.copy(
                selectedAllergies = newSelection,
                filteredRestaurants = filteredList
            )
        }
    }

    // ✅ ИЗМЕНЕНИЕ: Эта функция теперь "чистая" (pure function)
    // Она не меняет состояние, а только возвращает отфильтрованный список
    private fun applyFilters(restaurants: List<Restaurant>, selectedAllergies: Set<Allergy>): List<Restaurant> {
        if (selectedAllergies.isEmpty()) {
            return restaurants
        }
        return restaurants.filter { restaurant ->
            restaurant.allergies.none { it in selectedAllergies }
        }
    }

    fun onRestaurantSelected(restaurant: Restaurant) {
        _uiState.update { it.copy(selectedRestaurant = restaurant) }
    }

    fun onInfoWindowClosed() {
        _uiState.update { it.copy(selectedRestaurant = null) }
    }
}