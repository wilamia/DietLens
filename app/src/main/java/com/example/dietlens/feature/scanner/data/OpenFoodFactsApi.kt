package com.example.dietlens.feature.scanner.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {
    @GET("api/v2/product/{barcode}.json")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): Response<ProductResponse>


    @GET("category/{category}.json")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 5
    ): Response<CategoryResponse>

    @GET("cgi/search.pl?json=1&action=process")
    suspend fun searchAllProducts(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int = 5
    ): Response<CategoryResponse>
}


