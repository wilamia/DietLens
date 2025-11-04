package com.example.dietlens.feature.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun OnboardingGate(
    vm: OnboardingViewModel,
    onFinish: () -> Unit
) {
    val shown by vm.isOnboardingShown.collectAsState(initial = false)
    val scope = rememberCoroutineScope()

    if (shown) {
        // уже показывали — сразу уходим, БЕЗ LaunchedEffect внутри колбэка
        LaunchedEffect(Unit) { onFinish() }
        return
    }

    OnboardingScreen(
        viewModel = vm,
        onFinish = {
            scope.launch {
                vm.markOnboardingShown()
                onFinish()
            }
        }
    )
}
