package com.example.dietlens.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dietlens.feature.allergy.AllergySelectionScreen
import com.example.dietlens.feature.onboarding.presentation.OnboardingScreen
import com.example.dietlens.feature.onboarding.presentation.OnboardingViewModel
import com.example.dietlens.feature.splash.SplashScreen
import com.example.dietlens.feature.signin.presentation.LoginScreen
import com.example.dietlens.feature.signin.presentation.LoginViewModel
import com.example.dietlens.feature.signup.RegisterScreen
import com.example.dietlens.feature.signup.RegisterViewModel
import com.example.dietlens.core.base.ui.MainScreen
import com.example.dietlens.feature.forgot_password.ForgotPasswordScreen
import com.example.dietlens.feature.onboarding.presentation.OnboardingGate

object Routes {
    const val Splash = "splash"
    const val Onboarding = "onboarding"
    const val Login = "login"
    const val Register = "register"
    const val Main = "main"
    const val AllergySelection = "allergy_selection"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val onboardingVm: OnboardingViewModel = hiltViewModel()
    val vm: OnboardingViewModel = hiltViewModel()



    NavHost(navController = navController, startDestination = Routes.Splash) {

        composable(Routes.Splash) {
            SplashScreen(
                viewModel = onboardingVm,
                onLoggedIn = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                },
                onNotLoggedIn = {
                    navController.navigate(Routes.Onboarding) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Onboarding) {
            val vm: OnboardingViewModel = hiltViewModel()
            OnboardingGate(
                vm = vm,
                onFinish = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable("forgot") {
            ForgotPasswordScreen(
                viewModel = hiltViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Login) {
            val loginVm: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginVm,
                onLoginClick = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.Register)
                },
                onForgotPasswordClick = { navController.navigate("forgot") }
            )
        }

        composable(Routes.Register) {
            val registerVm: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = registerVm,
                onLoginClick = { navController.popBackStack() },
                onTermsClick = { /* TODO open terms */ },
                onSuccess = { uid ->
                    navController.navigate("${Routes.AllergySelection}/$uid") {
                        popUpTo(Routes.Register) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.AllergySelection}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            // DO NOT pass a VM if the composable uses hiltViewModel() by default
            AllergySelectionScreen(
                userId = userId,
                onDone = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Register) { inclusive = true }
                        popUpTo(Routes.Login) { inclusive = true } // сносим регистрацию из стека// сносим регистрацию из стека
                    }
                }
            )
        }

        composable(Routes.Main) {
            MainScreen(appNavController = navController)
        }
    }
}
