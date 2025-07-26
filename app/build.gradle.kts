import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

val secrets = Properties()
val secretFile = rootProject.file("secret.properties")

if (secretFile.exists()) {
    FileInputStream(secretFile).use { stream ->
        secrets.load(stream)
    }
} else {
    println("WARNING: secret.properties not found!")
}

android {
    namespace = "com.example.dietlens"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dietlens"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    defaultConfig {
        manifestPlaceholders["MAPS_API_KEY"] = secrets.getProperty("MAPS_API_KEY") ?: ""
        buildConfigField("String", "MAPS_API_KEY", "\"${secrets.getProperty("MAPS_API_KEY") ?: ""}\"")

    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Lifecycle + ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit + Moshi
    implementation(libs.retrofit)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.moshi.kotlin)

    // Room
    implementation (libs.androidx.room.runtime)
    kapt (libs.androidx.room.compiler)
    implementation( libs.androidx.room.ktx)

    // Auth0
    implementation (libs.auth0)
    implementation (libs.androidx.browser)

    // Barcode Scanner (MLKit)
    implementation (libs.barcode.scanning)

    // Google Maps Compose
    implementation (libs.maps.compose)
    implementation (libs.play.services.maps)

    // WorkManager
    implementation (libs.androidx.work.runtime.ktx)

    // DataStore
    implementation (libs.androidx.datastore.preferences)

    // Coil для Compose
    implementation(libs.coil.compose)

    // Accompanist
    implementation (libs.accompanist.systemuicontroller)
    implementation (libs.accompanist.permissions)

    //firebase
    implementation(libs.firebase.bom)
    implementation(libs.firebase.analytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}