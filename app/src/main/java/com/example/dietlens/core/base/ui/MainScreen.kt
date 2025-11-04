package com.example.dietlens.core.base.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    appNavController: NavHostController
) {
    BaseColumn(appNavController = appNavController)
}

