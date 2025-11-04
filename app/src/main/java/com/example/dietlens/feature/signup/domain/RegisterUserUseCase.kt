package com.example.dietlens.feature.signup.domain

import com.example.dietlens.feature.signup.User

// domain/RegisterUserUseCase.kt
class RegisterUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User, password: String): Result<String> {
        return repository.registerUser(user, password)
    }
}
