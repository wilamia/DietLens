package com.example.dietlens.feature.scanner.data

import androidx.compose.ui.graphics.Color

data class TagInfo(
    val id: String, // ID тега из API (например, "en:vegan")
    val displayText: String, // Текст для пользователя (например, "Vegan")
    val color: Color,
    val shadow: Boolean = false
)

// --- Список тегов, которые мы хотим распознавать и отображать ---
// Вы можете легко добавлять сюда новые теги
val supportedTags = listOf(
    TagInfo("en:gluten-free", "Gluten free", Color(0xFFC8E6C9), shadow = true),
    TagInfo("en:vegan", "Vegan", Color(0xFFC8E6C9)),
    TagInfo("en:vegetarian", "Vegetarian", Color(0xFFA5D6A7)),
    TagInfo("en:organic", "Organic", Color(0xFF81C784)),
    TagInfo("en:palm-oil-free", "Palm oil free", Color(0xFFB9F6CA)),
    TagInfo("en:fair-trade", "Fair Trade", Color(0xFFFFF9C4))
    // Добавьте больше тегов здесь...
)