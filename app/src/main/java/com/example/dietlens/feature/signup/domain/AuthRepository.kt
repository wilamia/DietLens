package com.example.dietlens.feature.signup.domain

import com.example.dietlens.feature.signup.User

interface AuthRepository {
    suspend fun registerUser(user: User, password: String): Result<Unit>
}