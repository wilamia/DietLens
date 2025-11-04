package com.example.dietlens.feature.restaurants
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun nearbyRestaurants(
        @Query("location") location: String, // "lat,lng"
        @Query("radius") radius: Int = 1500, // Радиус в метрах
        @Query("type") type: String = "restaurant",
        @Query("key") apiKey: String
    ): NearbySearchResponse
}