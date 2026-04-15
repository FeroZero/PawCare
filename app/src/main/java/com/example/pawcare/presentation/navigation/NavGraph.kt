package com.example.pawcare.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.presentation.home.HomeScreen
import com.example.pawcare.presentation.login.LoginScreen
import com.example.pawcare.presentation.register.PetConfirmationScreen
import com.example.pawcare.presentation.screens.pet.PetListScreen
import com.example.pawcare.presentation.screens.pet.PetProfileScreen
import com.example.pawcare.presentation.components.pets.PetViewModel
import com.example.pawcare.presentation.components.pets.PetUiEvent
import com.example.pawcare.presentation.components.products.ProductViewModel
import com.example.pawcare.presentation.register.PetRegisterScreen
import com.example.pawcare.presentation.screens.appointments.AppointmentConfirmationScreen
import com.example.pawcare.presentation.screens.appointments.ScheduleAppointmentScreen
import com.example.pawcare.presentation.screens.appointments.AppointmentListScreen
import com.example.pawcare.presentation.components.appointments.AppointmentViewModel
import com.example.pawcare.presentation.screens.product.InventoryListScreen
import com.example.pawcare.presentation.screens.product.ProductFormScreen

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
                onNavigateToPetList = { navController.navigate(Screen.PetList.route) },
                onNavigateToProduct = { navController.navigate(Screen.ProductList.route) },
                onNavigateToAppointments = { navController.navigate(Screen.PetList.route) },
                onNavigateToBilling = { /* TODO */ }
            )
        }

        composable(Screen.ScheduleAppointment.route) {
            ScheduleAppointmentScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegisterPet = { navController.navigate(Screen.PetRegister.route) },
                onNavigateToConfirmation = { date, time, pet, service ->
                    navController.navigate(Screen.AppointmentConfirmation.createRoute(date, time, pet, service)) {
                        popUpTo(Screen.ScheduleAppointment.route) { inclusive = true }
                    }
                },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToPets = { navController.navigate(Screen.PetList.route) },
                onNavigateToProduct = { navController.navigate(Screen.ProductList.route) },
                onNavigateToAppointments = { navController.navigate(Screen.PetList.route) }
            )
        }

        composable(
            route = Screen.AppointmentConfirmation.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType },
                navArgument("serviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val time = backStackEntry.arguments?.getString("time") ?: ""
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""

            AppointmentConfirmationScreen(
                date = date,
                time = time,
                petName = petName,
                serviceName = serviceName,
                onViewAppointments = { navController.navigate(Screen.PetList.route) },
                onScheduleAnother = {
                    navController.navigate(Screen.ScheduleAppointment.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onGoToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PetList.route) {
            val viewModel: AppointmentViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            AppointmentListScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToEdit = { /* TODO */ },
                onNavigateToSchedule = { navController.navigate(Screen.ScheduleAppointment.route) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToPets = { /* Already here */ },
                onNavigateToProducts = { navController.navigate(Screen.ProductList.route) }
            )
        }

        // --- MASCOTAS ---
        // (Maintaining existing Pet screens)
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
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToPets = { navController.navigate(Screen.PetList.route) },
                petRepository = petRepository
            )
        }
        
        composable(Screen.ProductList.route) {
            val viewModel: ProductViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            InventoryListScreen(
                state = state, 
                onEvent = viewModel::onEvent, 
                onNavigateToForm = { navController.navigate("product_form") },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToPets = { navController.navigate(Screen.PetList.route) },
                onNavigateToAppointments = { navController.navigate(Screen.PetList.route) }
            )
        }
    }
}
