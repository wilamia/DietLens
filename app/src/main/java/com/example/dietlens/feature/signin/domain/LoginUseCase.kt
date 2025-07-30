package com.example.dietlens.feature.signin.domain

class LoginUseCase(private val authRepository: LoginRepository) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.login(email, password)
    }

    suspend fun loginWithGoogle(idToken: String): Result<Unit> {
        return authRepository.loginWithGoogle(idToken)
    }
}