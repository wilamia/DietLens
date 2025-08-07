package com.example.dietlens.feature.signin.presentation

import android.content.Intent
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietlens.R
import com.example.dietlens.feature.signin.domain.LoginUseCase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<Int?>(null)
    val errorMessage: StateFlow<Int?> = _errorMessage

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        when {
            email.isBlank() && password.isBlank() -> {
                _errorMessage.value = R.string.login_password_email_error
                return
            }

            email.isBlank() -> {
                _errorMessage.value = R.string.login_email_error
                return
            }

            password.isBlank() -> {
                _errorMessage.value = R.string.login_password_error
                return
            }

        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = loginUseCase(email, password)
            _isLoading.value = false
            result.fold(
                onSuccess = {
                    _errorMessage.value = null
                    onSuccess()
                },
                onFailure = {
                    _errorMessage.value = R.string.login_incorrect_error
                }
            )
        }
    }

}