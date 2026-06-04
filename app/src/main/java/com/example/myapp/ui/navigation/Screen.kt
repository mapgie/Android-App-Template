package com.example.myapp.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")

    // Example screen with a required argument:
    // data object Detail : Screen("detail/{itemId}") {
    //     fun forItem(id: Long) = "detail/$id"
    // }
}
