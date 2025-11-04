package com.example.dietlens.core.base.ui

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)
