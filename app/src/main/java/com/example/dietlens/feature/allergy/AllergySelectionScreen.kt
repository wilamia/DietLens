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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dietlens.R
import com.example.dietlens.feature.signup.RegisterViewModel
import com.example.dietlens.theme.MontserratLight
import com.example.dietlens.theme.MontserratMedium
import com.example.dietlens.theme.MontserratSemiBold
import com.example.dietlens.theme.Purple
import kotlinx.coroutines.launch

@Composable
fun AllergySelectionScreen(
    viewModel: RegisterViewModel,
    userId: String,
    onComplete: () -> Unit
) {
    var gluten by rememberSaveable { mutableStateOf(false) }
    var lactose by rememberSaveable { mutableStateOf(false) }
    var nuts by rememberSaveable { mutableStateOf(false) }
    var seafood by rememberSaveable { mutableStateOf(false) }
    var eggs by rememberSaveable { mutableStateOf(false) }
    var soy by rememberSaveable { mutableStateOf(false) }
    var fruits by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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

        AllergyCheckbox(stringResource(id = R.string.allergy_gluten), gluten) { gluten = it }
        AllergyCheckbox(stringResource(id = R.string.allergy_lactose), lactose) { lactose = it }
        AllergyCheckbox(stringResource(id = R.string.allergy_nuts), nuts) { nuts = it }
        AllergyCheckbox(stringResource(id = R.string.allergy_seafood), seafood) { seafood = it }
        AllergyCheckbox(stringResource(id = R.string.allergy_eggs), eggs) { eggs = it }
        AllergyCheckbox(stringResource(id = R.string.allergy_soy), soy) { soy = it }
        AllergyCheckbox(stringResource(id = R.string.allergy_fruits), fruits) { fruits = it }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val allergies = AllergyPreferences(
                    gluten, lactose, nuts, seafood, eggs, soy, fruits
                )
                coroutineScope.launch {
                    viewModel.saveAllergyPreferences(userId, allergies) {
                        onComplete()
                    }
                }
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
            Text(stringResource(id = R.string.button_continue), fontSize = 18.sp, fontFamily = MontserratSemiBold)
        }
    }
}
@Composable
fun AllergyCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 16.sp, fontFamily = MontserratLight)
    }
}