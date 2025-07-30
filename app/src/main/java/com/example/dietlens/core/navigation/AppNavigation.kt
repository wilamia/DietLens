package com.example.dietlens.core.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.dietlens.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.dietlens.feature.onboarding.presentation.OnboardingScreen
import com.example.dietlens.feature.onboarding.presentation.OnboardingViewModel
import com.example.dietlens.feature.splash.SplashScreen
import com.example.dietlens.feature.signin.presentation.LoginScreen
import com.example.dietlens.feature.signin.presentation.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    val context = LocalContext.current

    val viewModel: OnboardingViewModel = hiltViewModel()

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id)) // Твой client_id из google-services.json
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

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
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onLoginClick = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { /* Навигация к регистрации */ },
                onForgotPasswordClick = { /* ... */ },
                googleSignInClient = googleSignInClient // Передаем клиент в LoginScreen
            )
        }

        composable("main") {
            MainScreen(
                onLogoutClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(onLogoutClick: () -> Unit) {
    Scaffold(
        // твой UI
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Добро пожаловать!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onLogoutClick) {
                Text("Выйти")
            }
        }
    }
}

