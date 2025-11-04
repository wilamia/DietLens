package com.example.dietlens.core.base.ui

import ProductsRoute
import android.R.attr.navigationIcon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
// ИСПОЛЬЗУЕМ ЭТУ ВЕРСИЮ 'ArrowBack'
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon // <-- ИСПРАВЛЕННЫЙ ИМПОРТ ICON
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar // <-- ДОБАВЛЕН ИМПОРТ
import androidx.compose.material3.TopAppBarDefaults // <-- ДОБАВЛЕН ИМПОРТ
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dietlens.R
import com.example.dietlens.core.navigation.Routes
import com.example.dietlens.feature.restaurants.RestaurantsMapScreenPerm
import com.example.dietlens.feature.home.ProductDetailsRoute
import com.example.dietlens.feature.profile.ProfileRoute
import com.example.dietlens.feature.scanner.ui.ScanScreen
import com.example.dietlens.theme.Buttons
import com.example.dietlens.theme.DarkText
import com.example.dietlens.theme.MontserratBold
import com.example.dietlens.theme.Navigation
import com.example.dietlens.theme.OnPrimary

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // Добавим OptIn, так как Scaffold в M3 экспериментальный
@Composable
fun BaseColumn(
    modifier: Modifier = Modifier,
    appNavController: NavHostController,
) {
    val bottomBarNavController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("home", Icons.Default.Home) { bottomBarNavController.navigate("home") },
        BottomNavItem("search", Icons.Default.QrCodeScanner) { bottomBarNavController.navigate("search") },
        BottomNavItem("cart", Icons.Default.Restaurant) { bottomBarNavController.navigate("cart") },
        BottomNavItem("settings", Icons.Default.Person) { bottomBarNavController.navigate("settings") },
    )

    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedIndex = bottomNavItems.indexOfFirst { it.title == currentRoute }.coerceAtLeast(0)

    val isDetailScreen = currentRoute?.startsWith("product_details") == true

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            stringResource(R.string.app_name),
                            fontSize = 24.sp,
                            color = Buttons,
                        )
                    },
                    navigationIcon = {
                        if (isDetailScreen) {
                            IconButton(onClick = { bottomBarNavController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Назад",
                                    tint = Buttons
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Color.Black.copy(alpha = 0.1f)
                )
            }
        },
        bottomBar = {
            BaseBottomNavigation(
                selectedIndex = selectedIndex,
                items = bottomNavItems
            )
        },
        containerColor = Navigation
    ) { innerPadding ->
        NavHost(
            navController = bottomBarNavController,
            startDestination = "home",
            Modifier.padding(top = innerPadding.calculateTopPadding()).background(OnPrimary)
        ) {

            composable("home") {
                ProductsRoute(
                    onProductClick = { barcode ->
                        bottomBarNavController.navigate("product_details/$barcode")
                    }
                )
            }
            composable(
                route = "product_details/{barcode}",
                arguments = listOf(navArgument("barcode") { type = NavType.StringType })
            ) { backStackEntry ->
                val barcode = backStackEntry.arguments?.getString("barcode")
                if (barcode != null) {
                    ProductDetailsRoute(
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    )
                }
            }
            composable("search") {
                ScanScreen()
            }
            composable("cart") { RestaurantsMapScreenPerm() }
            composable("settings") {
                ProfileRoute(
                    onNavigateToLogin = {
                        appNavController.navigate(Routes.Login) {
                            popUpTo(0)
                        }
                    },
                    onOpenProductDetails = { barcode ->
                        bottomBarNavController.navigate("product_details/$barcode")
                    }
                )
            }
        }

    }
}
