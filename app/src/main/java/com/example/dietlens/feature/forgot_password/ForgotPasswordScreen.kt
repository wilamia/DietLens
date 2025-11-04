package com.example.dietlens.feature.forgot_password

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietlens.R
import com.example.dietlens.theme.*

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successEmail by viewModel.successEmail.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OnPrimary)
            .padding(top = 10.dp, start = 30.dp, end = 30.dp, bottom = 58.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.recover_title),
                fontSize = 32.sp,
                color = DarkText,
                fontFamily = MontserratSemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.recovery_explanation),
                fontFamily = MontserratLight,
                color = DarkText,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 40.dp),
                textAlign = TextAlign.Center
            )

            if (successEmail == null) {
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (error != null) viewModel.clearError()
                    },
                    placeholder = {
                        Text(stringResource(R.string.enter_mail), color = Gray70, fontFamily = MontserratLight)
                    },
                    trailingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0x30000000))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Buttons.copy(alpha = 0.2f),
                        unfocusedContainerColor = Buttons.copy(alpha = 0.2f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                if (!error.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(error!!, color = Color.Red, fontFamily = MontserratLight)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.sendReset(email.trim()) },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Buttons,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(stringResource(R.string.send_link), fontSize = 16.sp, fontFamily = MontserratSemiBold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = Buttons,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.send_email),
                    fontSize = 24.sp,
                    color = DarkText,
                    fontFamily = MontserratSemiBold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.check_email, successEmail!!),
                    fontFamily = MontserratLight,
                    color = DarkText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
                OutlinedButton (
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Buttons,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.back_to_login), fontSize = 16.sp, fontFamily = MontserratSemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.back_to_login),
                color = Buttons,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onBack() }
            )
        }
    }
}
