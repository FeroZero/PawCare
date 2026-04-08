package com.example.pawcare.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object PetRegister : Screen("pet_register")
    object PetConfirmation : Screen("pet_confirmation/{petId}") {
        fun createRoute(petId: String) = "pet_confirmation/$petId"
    }
}
