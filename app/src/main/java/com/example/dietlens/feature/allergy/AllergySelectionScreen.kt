package com.example.dietlens.feature.allergy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.dietlens.R
import com.example.dietlens.feature.signup.RegisterViewModel
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.DarkText
import com.example.dietlens.theme.MontserratLight
import com.example.dietlens.theme.MontserratSemiBold
import kotlinx.coroutines.launch

@Composable
fun AllergySelectionScreen(
    userId: String,
    onDone: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {

    var checks by rememberSaveable {
        mutableStateOf(ALLERGY_ITEMS.associate { it.key to false })
    }

    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(id = R.string.allergy_screen_title),
            fontFamily = MontserratSemiBold,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp, top = 50.dp)
        )

        ALLERGY_ITEMS.forEach { item ->
            AllergyCheckbox(
                label = stringResource(id = item.titleRes),
                checked = checks[item.key] == true,
            ) { newValue ->
                checks = checks.toMutableMap().apply { this[item.key] = newValue }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val prefs = AllergyPreferences(
                    gluten = checks["gluten"] == true,
                    lactose = checks["lactose"] == true,
                    nuts = checks["nuts"] == true,
                    seafood = checks["seafood"] == true,
                    eggs = checks["eggs"] == true,
                    soy = checks["soy"] == true,
                    fruits = checks["fruits"] == true,
                )
                scope.launch { viewModel.saveAllergyPreferences(userId, prefs, onDone) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Buttons,
                contentColor = Color.White
            )
        ) {
            Text(
                stringResource(id = R.string.button_continue),
                fontSize = 18.sp,
                fontFamily = MontserratSemiBold
            )
        }

        if (state.error != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = state.error ?: "",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AllergyCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked, onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Buttons,
                uncheckedColor = Color.LightGray,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 16.sp, fontFamily = MontserratLight, color = DarkText)
    }
}