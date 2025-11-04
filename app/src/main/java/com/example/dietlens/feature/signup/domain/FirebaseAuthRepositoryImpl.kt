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

    // domain/FirebaseAuthRepositoryImpl.kt
    override suspend fun registerUser(user: User, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(user.email, password)
                .await()

            val uid = authResult.user?.uid ?: throw Exception("User UID is null")

            // Обновим displayName — не обязаловка, но приятно
            authResult.user?.updateProfile(
                userProfileChangeRequest { displayName = user.fullName }
            )?.await()

            // Создаём профиль пользователя с пустыми аллергиями
            val userData = mapOf(
                "fullName" to user.fullName,
                "email" to user.email,
                "phone" to user.phone,
                "allergies" to emptyMap<String, Boolean>()
            )
            firestore.collection("users").document(uid)
                .set(userData) // первый раз — set
                .await()

            // Важно: createUserWithEmailAndPassword уже АВТО-логинит
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ✅ --- РЕАЛИЗАЦИЯ НОВЫХ МЕТОДОВ ---

    /**
     * Просто выходит из Firebase Auth.
     */
    override fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * Удаляет данные пользователя из Firestore и сам аккаунт из Firebase Auth.
     */
    override suspend fun deleteAccount(): Result<Unit> {
        // Получаем текущего пользователя
        val user = firebaseAuth.currentUser
            ?: return Result.failure(Exception("Пользователь не авторизован"))

        val uid = user.uid

        return try {
            // 1. Сначала удаляем данные из Firestore
            firestore.collection("users").document(uid).delete().await()
            println("User data deleted from Firestore")

            // 2. Затем удаляем аккаунт из Auth
            user.delete().await()
            println("User account deleted from Auth")

            Result.success(Unit)
        } catch (e: Exception) {
            println("Error during account deletion: ${e.message}")
            // Firebase может потребовать недавний вход в систему для удаления
            // (e.g., com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException)
            Result.failure(e)
        }
    }
}