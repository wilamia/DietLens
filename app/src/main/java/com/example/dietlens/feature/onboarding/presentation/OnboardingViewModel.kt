package com.example.dietlens.feature.onboarding.presentation


import androidx.lifecycle.ViewModel
import com.example.dietlens.R
import com.example.dietlens.feature.onboarding.OnboardingPrefs
import com.example.dietlens.feature.onboarding.data.OnboardingPage
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow


@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val prefs: OnboardingPrefs
) : ViewModel() {
    val pages = com.example.dietlens.feature.onboarding.data.pages

    var currentPage = 0

    val isOnboardingShown = prefs.isShown

    suspend fun markOnboardingShown() = prefs.markShown()
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null
}

