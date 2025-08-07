package com.example.dietlens.feature.signup.domain

import com.example.dietlens.feature.signup.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun registerUser(user: User, password: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
            println("User created with uid: ${authResult.user?.uid}")

            val profileUpdates = userProfileChangeRequest {
                displayName = user.fullName
            }
            authResult.user?.updateProfile(profileUpdates)?.await()
            println("Profile updated")

            val uid = authResult.user?.uid ?: throw Exception("User UID is null")
            val userData = mapOf(
                "fullName" to user.fullName,
                "email" to user.email,
                "phone" to user.phone
            )
            firestore.collection("users").document(uid).set(userData).await()
            println("User data saved to Firestore")

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error during registration: ${e.message}")
            Result.failure(e)
        }
    }
}