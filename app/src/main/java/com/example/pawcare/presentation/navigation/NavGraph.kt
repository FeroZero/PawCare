package com.example.pawcare.presentation.navigation

import androidx.activity.result.launch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.presentation.components.owners.OwnerUiEvent
import com.example.pawcare.presentation.components.owners.OwnerViewModel
import com.example.pawcare.presentation.home.HomeScreen
import com.example.pawcare.presentation.login.LoginScreen
import com.example.pawcare.presentation.register.PetConfirmationScreen
import com.example.pawcare.presentation.screens.pet.PetListScreen
import com.example.pawcare.presentation.screens.pet.PetProfileScreen
import com.example.pawcare.presentation.components.pets.PetViewModel
import com.example.pawcare.presentation.components.pets.PetUiEvent
import com.example.pawcare.presentation.components.products.ProductViewModel
import com.example.pawcare.presentation.register.PetRegisterScreen
import com.example.pawcare.presentation.screens.product.InventoryListScreen
import com.example.pawcare.presentation.screens.product.ProductFormScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    petRepository: PetRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // --- LOGIN ---
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- DASHBOARD ---
        composable(Screen.Home.route) {
            HomeScreen(

                onNavigateToRegisterPet = { navController.navigate(Screen.PetRegister.route) },
                onNavigateToPetList = { navController.navigate(Screen.PetList.route) },
                onNavigateToProduct = { navController.navigate(Screen.ProductList.route) },
                onNavigateToAppointments = { /* TODO */ },
                onNavigateToBilling = { /* TODO */ }
            )
        }

        // --- LISTA DE MASCOTAS ---
        composable(Screen.PetList.route) {
            val viewModel: PetViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            PetListScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        is PetUiEvent.OnPetClick -> {
                            navController.navigate(Screen.PetProfile.createRoute(event.petId))
                        }
                        else -> viewModel.onEvent(event)
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.PetRegister.route) },
                onBack = { navController.popBackStack() }
            )
        }

        // --- PERFIL DE MASCOTA ---
        composable(
            route = Screen.PetProfile.route,
            arguments = listOf(navArgument("petId") { type = NavType.StringType })
        ) { backStackEntry ->            val petId = backStackEntry.arguments?.getString("petId") ?: ""

            // AGREGAR ESTA LÍNEA AQUÍ:
            val scope = androidx.compose.runtime.rememberCoroutineScope()

            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.PetList.route) }
            val petViewModel: PetViewModel = hiltViewModel(parentEntry)
            val ownerViewModel: OwnerViewModel = hiltViewModel()

            val petState by petViewModel.state.collectAsState()
            val ownerState by ownerViewModel.state.collectAsState()

            val pet = petState.pets.find { it.id == petId }

            LaunchedEffect(pet) {
                pet?.let {
                    ownerViewModel.onEvent(OwnerUiEvent.OnOwnerClick(it.ownerId))
                }
            }

            pet?.let { petData ->
                val realOwner = ownerState.selectedOwner

                PetProfileScreen(
                    pet = petData,
                    owner = realOwner ?: com.example.pawcare.domain.model.Owner(
                        id = petData.ownerId,
                        fullName = petData.ownerName,
                        phone = "Cargando...",
                        email = "",
                        address = "",
                        isVip = false,
                        createdAt = ""
                    ),
                    onBack = { navController.popBackStack() },
                    onEditClick = { id ->
                        navController.navigate("${Screen.PetRegister.route}?petId=$id")
                    },
                    onDeleteClick = { pId, oId ->
                        scope.launch {
                            petViewModel.onEvent(PetUiEvent.OnDeletePet(pId))

                            delay(500)

                            ownerViewModel.onEvent(OwnerUiEvent.OnDeleteOwner(oId))

                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        // --- REGISTRO / EDICIÓN DE MASCOTA ---
        composable(
            route = "${Screen.PetRegister.route}?petId={petId}",
            arguments = listOf(
                navArgument("petId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            PetRegisterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfirmation = { petId ->
                    navController.navigate(Screen.PetConfirmation.createRoute(petId)) {
                        popUpTo(Screen.PetRegister.route) { inclusive = true }
                    }
                }
            )
        }

        // --- CONFIRMACIÓN ---
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

        // --- PRODUCTOS ---
        composable(Screen.ProductList.route) {
            val viewModel: ProductViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            InventoryListScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToForm = {
                    navController.navigate("product_form")
                }
            )
        }

        composable("product_form") {
            val viewModel: ProductViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            ProductFormScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = { navController.popBackStack() },
                isEdit = state.name.isNotEmpty()
            )
        }
    }
}
