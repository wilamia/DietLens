package com.example.dietlens.feature.signin.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dietlens.R
import com.example.dietlens.theme.Gray20
import com.example.dietlens.theme.Gray70
import com.example.dietlens.theme.MontserratLight
import com.example.dietlens.theme.MontserratMedium
import com.example.dietlens.theme.MontserratSemiBold
import com.example.dietlens.theme.Purple
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    googleSignInClient: GoogleSignInClient
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val uiErrorMessage by viewModel.errorMessage.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.handleGoogleSignInResult(intent, onLoginClick)
            }
        } else {
            viewModel.sendGoogleLoginError(GoogleSignInStatusCodes.SIGN_IN_CANCELLED)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 10.dp, start = 30.dp, end = 30.dp, bottom = 58.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.login_welcome_back),
                fontSize = 24.sp,
                color = Color(0xFF252525),
                fontFamily = MontserratMedium
            )
            Text(
                stringResource(R.string.login_access_motivation),
                fontFamily = MontserratLight,
                color = Color(0xFF252525),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 95.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = {
                    Text(
                        stringResource(R.string.login_enter_email),
                        color = Gray70,
                        fontFamily = MontserratLight
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0x30000000)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Gray20,
                    unfocusedContainerColor = Gray20,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(25.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                placeholder = {
                    Text(
                        stringResource(R.string.login_password),
                        color = Gray70,
                        fontFamily = MontserratLight
                    )
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0x30000000)
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Gray20,
                    unfocusedContainerColor = Gray20,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.login_forgot_password),
                    color = Color(0xFF6C63FF),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.clickable { onForgotPasswordClick() }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            uiErrorMessage?.let {
                Text(
                    text = stringResource(id = it),
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(23.dp))

            Button(
                onClick = {
                    viewModel.login(email, password) {
                        onLoginClick()
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B5BFF),
                    contentColor = Color.White
                )
            ) {
                Text(
                    stringResource(R.string.login_next),
                    fontSize = 16.sp,
                    fontFamily = MontserratSemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(23.dp))
            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google_logo),
                    contentDescription = stringResource(R.string.google_sign_in),
                    modifier = Modifier.size(24.dp),
                    tint = Purple
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.sign_in_with_google),
                    fontSize = 16.sp,
                    fontFamily = MontserratSemiBold
                )
            }


            Spacer(modifier = Modifier.height(23.dp))

            Row {
                Text(
                    stringResource(R.string.login_new_member),
                    fontSize = 14.sp,
                    fontFamily = MontserratMedium
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(R.string.login_register_now),
                    fontSize = 14.sp,
                    color = Color(0xFF6C63FF),
                    fontFamily = MontserratSemiBold,
                    modifier = Modifier.clickable { onRegisterClick() }
                )
            }

        }
    }
}
