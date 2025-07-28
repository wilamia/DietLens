package com.example.dietlens.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.dietlens.R

val MontserratLight = FontFamily(
    Font(R.font.montserrat_light)
)

val MontserratMedium = FontFamily(
    Font(R.font.montserrat_medium)
)

val MontserratSemiBold = FontFamily(
    Font(R.font.montserrat_semibold)
)

val MontserratBold = FontFamily(
    Font(R.font.montserrat_bold)
)

val MontserratRegular = FontFamily(
    Font(R.font.montserrat_regular)
)

@Composable
fun DietLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}