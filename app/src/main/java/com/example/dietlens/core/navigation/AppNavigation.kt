package com.example.dietlens.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dietlens.feature.onboarding.presentation.OnboardingScreen
import com.example.dietlens.feature.onboarding.presentation.OnboardingViewModel
import com.example.dietlens.feature.onboarding.presentation.SplashScreen



@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val viewModel: OnboardingViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "splash") {

        composable("splash") {
            SplashScreen(
                viewModel = viewModel,
                onLoggedIn = {
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNotLoggedIn = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingScreen(
                viewModel = viewModel,
                onFinish = {
                    navController.navigate("auth") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("auth") {
           // переход на страницу авторизации
        }

        composable("main") {
            // переход на главную страницу
        }
    }
}


