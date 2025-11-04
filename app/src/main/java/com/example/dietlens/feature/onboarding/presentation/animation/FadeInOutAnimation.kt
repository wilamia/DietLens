package com.example.dietlens.feature.onboarding.presentation.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun FadeInOutAnimation(
    visible: Boolean,
    durationMillis: Int = 1000,
    onAnimationEnd: () -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    var alphaTarget by remember { mutableStateOf(if (visible) 1f else 0f) }
    val alpha by animateFloatAsState(
        targetValue = alphaTarget,
        animationSpec = tween(durationMillis),
        finishedListener = {
            if (!visible) onAnimationEnd()
        }
    )

    LaunchedEffect(visible) {
        alphaTarget = if (visible) 1f else 0f
    }

    content(Modifier.alpha(alpha))
}
