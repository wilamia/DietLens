package com.example.dietlens.feature.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietlens.feature.signup.domain.AuthRepository // ⚠️ ПРЕДПОЛАГАЕМ, ЧТО ЭТО ВАШ РЕПОЗИТОРИЙ
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // ✅ Внедряем зависимости, которые нам нужны
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private var favoritesListener: ListenerRegistration? = null
    private var historyListener: ListenerRegistration? = null
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserData()
        loadPreferences()
        observeFavorites()
        observeHistory()
    }
    override fun onCleared() {
        favoritesListener?.remove()
        historyListener?.remove()
        super.onCleared()
    }
    private fun loadUserData() {
        _uiState.update { it.copy(isLoading = true) }
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Пользователь не найден") }
            return
        }

        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                if (!document.exists()) {
                    Log.w("ProfileViewModel", "User doc missing for uid=$userId")
                }
                val name = document.getString("fullName")
                val id = userId
                val fallbackName = firebaseAuth.currentUser?.email ?: "Пользователь"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = name ?: fallbackName,
                        userId = id
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to load user data", e)
                _uiState.update { it.copy(isLoading = false, error = "Не удалось загрузить данные") }
            }
        }
    }




    fun onUpdateUserName(newName: String) {
        if (newName.isBlank()) {
            _uiState.update { it.copy(error = "Имя не может быть пустым") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, error = "Пользователь не найден") }
            return
        }

        viewModelScope.launch {
            try {
                // Обновляем в Firestore
                firestore.collection("users").document(userId)
                    .update("fullName", newName)
                    .await()

                // Обновляем локально и прячем диалог
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = newName,
                        showChangeNameDialog = false
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Failed to update name", e)
                _uiState.update {
                    it.copy(isLoading = false, error = "Ошибка обновления имени")
                }
            }
        }
    }
    fun loadPreferences() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snap = firestore.collection("users").document(uid).get().await()
                val map = (snap.get("allergies") as? Map<*, *>)?.mapNotNull {
                    val k = it.key as? String
                    val v = it.value as? Boolean
                    if (k != null && v != null) k to v else null
                }?.toMap().orEmpty()
                _uiState.update { it.copy(allergies = map) }
            } catch (_: Exception) {
                // не ломаем UI, просто оставляем пусто
            }
        }
    }

    fun onSavePreferences(checks: Map<String, Boolean>) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingPreferences = true, error = null) }
            try {
                firestore.collection("users")
                    .document(uid)
                    .set(mapOf("allergies" to checks), com.google.firebase.firestore.SetOptions.merge())
                    .await()
                _uiState.update { it.copy(isSavingPreferences = false, allergies = checks) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSavingPreferences = false, error = "Не удалось сохранить: ${e.message}") }
            }
        }
    }
    fun onLogoutClick() {
        Log.d("ProfileViewModel", "Logging out...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Вызываем метод из репозитория
                authRepository.logout()
                // Отправляем событие о выходе
                _uiState.update { it.copy(isLoading = false, navigationEvent = NavigationEvent.NavigateToLogin) }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Logout failed", e)
                _uiState.update { it.copy(isLoading = false, error = "Ошибка выхода: ${e.message}") }
            }
        }
    }

    fun onDeleteAccountClick() {
        Log.d("ProfileViewModel", "Deleting account...")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Вызываем метод из репозитория
                authRepository.deleteAccount()
                // Отправляем событие о выходе (т.к. аккаунт удален)
                _uiState.update { it.copy(isLoading = false, navigationEvent = NavigationEvent.NavigateToLogin) }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Delete account failed", e)
                _uiState.update { it.copy(isLoading = false, error = "Ошибка удаления: ${e.message}") }
            }
        }
    }
    private fun observeFavorites() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        favoritesListener?.remove()
        favoritesListener = firestore.collection("users")
            .document(uid)
            .collection("likedProducts")
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("ProfileViewModel", "favorites listen error", err)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.map { d ->
                    FavoriteProduct(
                        productId = d.id,
                        name = d.getString("name") ?: "Без названия",
                        imageUrl = d.getString("imageUrl"),
                        brand = d.getString("brand"),
                        liked = true
                    )
                }.orEmpty()
                _uiState.update { it.copy(favorites = list) }
            }
    }

    fun toggleFavorite(productId: String, name: String, imageUrl: String?, brand: String? = null) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(uid)
            .collection("likedProducts").document(productId)

        viewModelScope.launch {
            // mark loading for this id
            _uiState.update { it.copy(updatingFavoriteIds = it.updatingFavoriteIds + productId) }
            try {
                val snap = docRef.get().await()
                if (snap.exists()) {
                    docRef.delete().await() // dislike
                } else {
                    docRef.set(
                        mapOf(
                            "name" to name,
                            "imageUrl" to imageUrl,
                            "brand" to brand,
                            "likedAt" to com.google.firebase.Timestamp.now()
                        )
                    ).await() // like
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "toggleFavorite failed", e)
            } finally {
                _uiState.update { it.copy(updatingFavoriteIds = it.updatingFavoriteIds - productId) }
            }
        }
    }
    private fun observeHistory() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        historyListener?.remove()
        historyListener = firestore.collection("users")
            .document(uid)
            .collection("scanHistory")
            .orderBy("scannedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("ProfileViewModel", "history listen error", err)
                    return@addSnapshotListener
                }
                val list = snap?.documents?.map { d ->
                    ScanHistoryEntry(
                        docId = d.id,
                        barcode = d.getString("barcode") ?: "",
                        name = d.getString("productName") ?: "Без названия",
                        imageUrl = d.getString("imageUrl"),
                        detectedAllergens = (d.get("detectedAllergens") as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                        scannedAt = d.getTimestamp("scannedAt")
                    )
                }.orEmpty()
                _uiState.update { it.copy(history = list) }
            }
    }

    fun deleteHistoryItem(docId: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val docRef = firestore.collection("users").document(uid)
            .collection("scanHistory").document(docId)

        viewModelScope.launch {
            _uiState.update { it.copy(deletingHistoryIds = it.deletingHistoryIds + docId) }
            try {
                docRef.delete().await()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "deleteHistoryItem failed", e)
            } finally {
                _uiState.update { it.copy(deletingHistoryIds = it.deletingHistoryIds - docId) }
            }
        }
    }
    fun onNavigationEventHandled() {
        _uiState.update { it.copy(navigationEvent = null, error = null) }
    }
}