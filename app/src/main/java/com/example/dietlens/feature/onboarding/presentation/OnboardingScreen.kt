package com.example.dietlens.feature.onboarding.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietlens.R
import com.example.dietlens.feature.onboarding.presentation.animation.FadeInOutAnimation
import com.example.dietlens.theme.ActiveDot
import com.example.dietlens.theme.InactiveDot
import com.example.dietlens.theme.WhiteBackground
import com.example.dietlens.theme.MontserratBold
import com.example.dietlens.theme.MontserratLight

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableStateOf(viewModel.currentPage) }
    var visible by remember { mutableStateOf(true) }

    FadeInOutAnimation(
        visible = visible,
        durationMillis = 500,
        onAnimationEnd = {
            if (!visible) onFinish()
        }
    ) { alphaModifier ->

        Box(modifier = modifier
            .fillMaxSize()
            .then(alphaModifier)) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    (slideInHorizontally(tween(500)) { fullWidth -> fullWidth } + fadeIn(tween(500))) togetherWith
                            (slideOutHorizontally(tween(500)) { fullWidth -> -fullWidth } + fadeOut(tween(500)))
                }
            ) { targetPage ->
                val targetPageData = viewModel.pages[targetPage]

                OnboardingCard(
                    imageRes = targetPageData.imageRes,
                    title = stringResource(id = targetPageData.titleRes),
                    subtitle = stringResource(id = targetPageData.subtitleRes),
                    currentPage = targetPage,
                    totalPages = viewModel.pages.size,
                    onNextClick = {
                        if (targetPage == viewModel.pages.lastIndex) {
                            visible = false
                        } else {
                            currentPage = targetPage + 1
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Composable
fun OnboardingCard(
    imageRes: Int,
    title: String,
    subtitle: String,
    currentPage: Int,
    totalPages: Int,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(WhiteBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(220.dp)
            )
            Spacer(Modifier.height(86.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                lineHeight = 40.sp,
                color = Color.Black,
                fontFamily = MontserratBold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 24.sp,
                color = Color.Gray,
                fontFamily = MontserratLight,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(36.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                DotsIndicator(selectedIndex = currentPage, totalDots = totalPages)
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.Black
                ) {
                    IconButton(onClick = onNextClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = stringResource(id = R.string.onboarding_next),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun DotsIndicator(selectedIndex: Int, totalDots: Int) {
    Row {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .width(16.dp)
                    .height(if (index == selectedIndex) 8.dp else 6.dp)
                    .background(
                        color = if (index == selectedIndex) ActiveDot else InactiveDot,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }
    }
}


