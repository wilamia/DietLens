package com.example.dietlens.feature.signup

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dietlens.BuildConfig
import com.example.dietlens.theme.MontserratLight
import com.example.dietlens.theme.MontserratMedium
import com.example.dietlens.theme.MontserratSemiBold
import com.example.dietlens.theme.Purple
import com.example.dietlens.theme.Gray20
import com.example.dietlens.theme.Gray70
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException


@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var agreedToTerms by rememberSaveable { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 159.dp, start = 30.dp, end = 30.dp, bottom = 41.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Get Started",
                fontSize = 36.sp,
                color = Color.Black,
                fontFamily = MontserratSemiBold
            )

            Text(
                text = "by creating a free account.",
                fontSize = 14.sp,
                fontFamily = MontserratLight,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 36.dp)
            )

            CustomInputField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Full name",
                iconVector = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(19.dp))
            CustomInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Valid email",
                iconVector = Icons.Default.Email
            )
            Spacer(modifier = Modifier.height(19.dp))
            CustomInputField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = "Phone number",
                iconVector = Icons.Default.Phone
            )
            Spacer(modifier = Modifier.height(19.dp))
            CustomInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Strong Password",
                iconVector = Icons.Default.Lock
            )
            Spacer(modifier = Modifier.height(17.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = agreedToTerms,
                    onCheckedChange = { agreedToTerms = it }
                )
                Column {
                    Text(
                        text = "By checking the box you agree to our ",
                        fontSize = 13.sp,
                        fontFamily = MontserratLight
                    )
                    Text(
                        text = "Terms and Conditions.",
                        fontSize = 13.sp,
                        color = Purple,
                        fontFamily = MontserratMedium,
                        modifier = Modifier.clickable { onTermsClick() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.error != null) {
                Text(
                    text = state.error ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            Button(
                onClick = {
                    viewModel.onRegisterClick(
                        fullName,
                        email,
                        phoneNumber,
                        password,
                        agreedToTerms
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White
                )
            ) {

                Text("Next", fontFamily = MontserratSemiBold, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(23.dp))

            Row {
                Text("Already a member?", fontFamily = MontserratMedium, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Log In",
                    color = Purple,
                    fontSize = 13.sp,
                    fontFamily = MontserratSemiBold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }

        }

    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    iconVector: ImageVector? = null,
    iconPainter: Painter? = null,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = Color.Gray, fontFamily = MontserratLight)
        },
        trailingIcon = {
            when {
                iconVector != null -> Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Gray70
                )

                iconPainter != null -> Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    tint = Gray70
                )
            }
        },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Gray20,
            focusedContainerColor = Gray20,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        )
    )
}
