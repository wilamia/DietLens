package com.example.dietlens.feature.signup.domain

import com.example.dietlens.feature.signup.User

class RegisterUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User, password: String): Result<Unit> {
        return repository.registerUser(user, password)
    }
}