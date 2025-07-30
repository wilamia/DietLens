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
    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = loginUseCase.loginWithGoogle(idToken)
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
    fun handleGoogleSignInResult(data: Intent?, onSuccess: () -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                loginWithGoogle(idToken, onSuccess)
            } else {
                _errorMessage.value = R.string.google_token_missing
            }
        } catch (e: ApiException) {
            sendGoogleLoginError(e.statusCode)
        }
    }

    fun sendGoogleLoginError(code: Int) {
        _errorMessage.value = when (code) {
            GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> R.string.google_sign_in_cancelled
            GoogleSignInStatusCodes.SIGN_IN_FAILED -> R.string.google_sign_in_failed
            GoogleSignInStatusCodes.NETWORK_ERROR -> R.string.google_sign_in_network_error
            else -> R.string.google_sign_in_unknown_error
        }
    }

}