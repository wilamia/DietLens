package com.example.dietlens.feature.profile

/**
 * Data-класс, описывающий состояние UI экрана профиля.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId : String = "",
    val userName: String = "Загрузка...", // Имя пользователя, с плейсхолдером
    val navigationEvent: NavigationEvent? = null,
    val showChangeNameDialog: Boolean = false,
    val allergies: Map<String, Boolean> = emptyMap(),
    val isSavingPreferences: Boolean = false,
    val favorites: List<FavoriteProduct> = emptyList(),     // 👈 НОВОЕ
    val updatingFavoriteIds: Set<String> = emptySet(),
    val history: List<ScanHistoryEntry> = emptyList(),
    val deletingHistoryIds: Set<String> = emptySet()
)

/**
 * Sealed-класс для "одноразовых" событий навигации.
 * UI обработает это событие, а затем ViewModel его "погасит".
 */
sealed class NavigationEvent {
    object NavigateToPreferences : NavigationEvent()
    object ShowChangeNameDialog : NavigationEvent()
    object NavigateToLogin : NavigationEvent() // Для выхода и удаления
}