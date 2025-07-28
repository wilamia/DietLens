package com.example.dietlens.feature.onboarding.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dietlens.R
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.sp
import com.example.dietlens.feature.onboarding.presentation.animation.FadeInAnimation
import com.example.dietlens.theme.DarkText
import com.example.dietlens.theme.MontserratBold
import com.example.dietlens.theme.Purple
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: OnboardingViewModel,
    onLoggedIn: () -> Unit,
    onNotLoggedIn: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(1500)
        if (viewModel.isUserLoggedIn()) {
            coroutineScope.launch { onLoggedIn() }
        } else {
            coroutineScope.launch { onNotLoggedIn() }
        }
    }

    FadeInAnimation {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple)
                .then(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_splash),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 24.sp,
                    color = DarkText,
                    fontFamily = MontserratBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
