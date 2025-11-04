package com.example.dietlens.feature.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successEmail = MutableStateFlow<String?>(null)
    val successEmail: StateFlow<String?> = _successEmail

    fun sendReset(email: String) {
        if (email.isBlank()) {
            _error.value = "Введите email"
            return
        }
        _error.value = null
        _isLoading.value = true
        viewModelScope.launch {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener {
                    _isLoading.value = false
                    if (it.isSuccessful) {
                        _successEmail.value = email
                    } else {
                        _error.value = it.exception?.message ?: "Не удалось отправить письмо"
                    }
                }
        }
    }

    fun clearError() { _error.value = null }
}
