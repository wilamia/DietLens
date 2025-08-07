package com.example.dietlens.feature.signup

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietlens.R
import com.example.dietlens.feature.allergy.AllergyPreferences
import com.example.dietlens.feature.signup.domain.RegisterUserUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow<RegisterUiState>(RegisterUiState())
    val state = _state.asStateFlow()

    fun onRegisterClick(fullName: String, email: String, phone: String, password: String, agreedToTerms: Boolean) {
        if (!agreedToTerms) {
            _state.value = _state.value.copy(error = "You must agree to the terms and conditions")
            return
        }

        val validationError = validateInput(fullName, email, phone, password)
        if (validationError != null) {
            _state.value = _state.value.copy(error = validationError)
            return
        }

        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val user = User(fullName, email, phone)
            val result = registerUserUseCase(user, password)

            _state.value = _state.value.copy(
                isLoading = false,
                isSuccess = result.isSuccess,
                error = result.exceptionOrNull()?.message
            )
        }
    }

    fun validateInput(fullName: String, email: String, phone: String, password: String): String? {
        if (fullName.isBlank()) return "Full name is required"
        if (!fullName.matches(Regex("^[a-zA-Zа-яА-ЯёЁ\\s'-]{2,50}\$"))) return "Full name contains invalid characters"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Invalid email address"
        if (!phone.matches(Regex("^\\+?[1-9][0-9]{9,14}\$"))) return "Invalid phone number"
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
        if (!passwordPattern.containsMatchIn(password)) {
            return "Password must be at least 8 characters and include an uppercase letter, a digit, and a special character"
        }
        return null
    }

    suspend fun saveAllergyPreferences(userId: String, preferences: AllergyPreferences, onDone: () -> Unit) {
            try {
                firestore.collection("users")
                    .document(userId)
                    .update("allergies", preferences)
                    .await()
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = "Failed to save allergy data: ${e.message}")
            }
        }
    }
