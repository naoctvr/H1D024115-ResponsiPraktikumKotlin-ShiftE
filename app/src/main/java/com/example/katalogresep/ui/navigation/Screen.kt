package com.example.katalogresep.ui.navigation


sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Detail : Screen("detail/{recipeId}") {
        fun createRoute(recipeId: String) = "detail/$recipeId"
    }
}
