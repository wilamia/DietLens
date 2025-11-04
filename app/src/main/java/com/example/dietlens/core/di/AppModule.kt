package com.example.dietlens.core.di


import com.example.dietlens.feature.restaurants.OpenFoodFactsRetrofit
import com.example.dietlens.feature.scanner.data.OpenFoodFactsApi
import com.example.dietlens.feature.scanner.data.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    val systemLanguageCode: String
        get() = Locale.getDefault().language

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // Эта функция будет определять, какой URL использовать
    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        val lang = Locale.getDefault().language // Например, "ru", "en"

        // Список языков, для которых у OpenFoodFacts есть свой поддомен
        val supportedSubdomains = setOf("ru", "fr", "de", "es", "it", "jp")

        val subdomain = if (supportedSubdomains.contains(lang)) lang else "world"

        return "https://$subdomain.openfoodfacts.org/"
    }

    // ... (Firebase провайдеры остаются без изменений) ...

    @Provides
    @Singleton
    @OpenFoodFactsRetrofit
    fun provideRetrofit(baseUrl: String): Retrofit { // <-- Теперь получаем URL как зависимость
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .readTimeout(30, TimeUnit.SECONDS) // (Это, скорее всего, ваша проблема)

            // (Опционально) Увеличиваем время на подключение
            .connectTimeout(30, TimeUnit.SECONDS)

            // (Опционально) Увеличиваем время на отправку данных
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideProductRepo(
        api: OpenFoodFactsApi,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ProductRepository {
        return ProductRepository(api, firestore, auth)
    }

    @Provides
    @Singleton
    fun provideOpenFoodFactsApi(@OpenFoodFactsRetrofit retrofit: Retrofit): OpenFoodFactsApi {
        return retrofit.create(OpenFoodFactsApi::class.java)
    }
}