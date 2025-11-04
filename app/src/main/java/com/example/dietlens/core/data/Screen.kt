package com.example.dietlens.core.data

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Scanner : Screen("scanner", "Scanner")
    object Settings : Screen("settings", "Settings")
    object Restaurants : Screen("restaurants", "Restaurants")
    object ProductDetail : Screen("productDetail", "Product Detail")
}
