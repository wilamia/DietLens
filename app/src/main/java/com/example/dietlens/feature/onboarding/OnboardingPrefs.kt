// core/prefs/OnboardingPrefs.kt
package com.example.dietlens.feature.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("app_prefs")

@Singleton
class OnboardingPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY = booleanPreferencesKey("onboarding_shown")

    val isShown: Flow<Boolean> = context.dataStore.data.map { it[KEY] ?: false }

    suspend fun markShown() {
        context.dataStore.edit { it[KEY] = true }
    }
}
