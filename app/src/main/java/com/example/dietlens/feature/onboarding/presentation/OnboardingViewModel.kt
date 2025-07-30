package com.example.dietlens.feature.onboarding.presentation


import androidx.lifecycle.ViewModel
import com.example.dietlens.R
import com.example.dietlens.feature.onboarding.data.OnboardingPage
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject


@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    val pages = listOf(
        OnboardingPage(
            R.drawable.img_product,
            R.string.onboarding_product_title,
            R.string.onboarding_product_subtitle
        ),
        OnboardingPage(
            R.drawable.img_heart,
            R.string.onboarding_favourite_title,
            R.string.onboarding_favourite_subtitle
        ),
        OnboardingPage(
            R.drawable.img_cafe,
            R.string.onboarding_cafe_title,
            R.string.onboarding_cafe_subtitle
        )
    )

    var currentPage = 0

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}
