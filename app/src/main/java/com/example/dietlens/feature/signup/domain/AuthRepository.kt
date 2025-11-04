package com.example.dietlens.feature.signup.domain

import com.example.dietlens.feature.signup.User

// domain/AuthRepository.kt
interface AuthRepository {
    suspend fun registerUser(user: User, password: String): Result<String> // 👈 вернём uid
    fun logout()
    suspend fun deleteAccount(): Result<Unit>
}
