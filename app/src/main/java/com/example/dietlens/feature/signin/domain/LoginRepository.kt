package com.example.dietlens.feature.signin.domain

interface LoginRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
}