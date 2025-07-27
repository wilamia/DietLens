package com.example.dietlens.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MontserratLight,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MontserratMedium,
        fontSize = 14.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MontserratSemiBold,
        fontSize = 20.sp
    )
)