package com.example.dietlens.feature.restaurants

import android.content.Context
import com.example.dietlens.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {

    @Provides
    @Singleton
    fun providePlacesClient(@ApplicationContext context: Context): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context.applicationContext, BuildConfig.MAPS_API_KEY)
        }
        return Places.createClient(context.applicationContext)
    }

    // ✅ RENAMED and simplified to one provider
    @Provides
    @Singleton
    @PlacesRetrofit
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val ok = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .client(ok)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePlacesApiService( @PlacesRetrofit retrofit: Retrofit): PlacesApiService {
        return retrofit.create(PlacesApiService::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OpenFoodFactsRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PlacesRetrofit