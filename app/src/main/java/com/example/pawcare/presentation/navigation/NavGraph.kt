package com.example.pawcare.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.presentation.home.HomeScreen
import com.example.pawcare.presentation.login.LoginScreen
import com.example.pawcare.presentation.register.PetConfirmationScreen
import com.example.pawcare.presentation.register.PetRegisterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    petRepository: PetRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRegisterPet = { navController.navigate(Screen.PetRegister.route) },
                onNavigateToPetList = { /* TODO */ },
                onNavigateToAppointments = { /* TODO */ },
                onNavigateToBilling = { /* TODO */ }
            )
        }
        
        composable(Screen.PetRegister.route) {
            PetRegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfirmation = { petId ->
                    navController.navigate(Screen.PetConfirmation.createRoute(petId)) {
                        popUpTo(Screen.PetRegister.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.PetConfirmation.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""
            PetConfirmationScreen(
                petId = petId,
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToPets = { navController.navigate(Screen.Home.route) },
                petRepository = petRepository
            )
        }
    }
}
