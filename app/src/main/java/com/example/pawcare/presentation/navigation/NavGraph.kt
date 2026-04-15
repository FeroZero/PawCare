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
import com.example.pawcare.domain.repository.PaymentRepository
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.presentation.components.owners.OwnerUiEvent
import com.example.pawcare.presentation.components.owners.OwnerViewModel
import com.example.pawcare.presentation.home.HomeScreen
import com.example.pawcare.presentation.login.LoginScreen
import com.example.pawcare.presentation.register.PetConfirmationScreen
import com.example.pawcare.presentation.screens.pet.PetListScreen
import com.example.pawcare.presentation.screens.pet.PetProfileScreen
import com.example.pawcare.presentation.components.pets.PetViewModel
import com.example.pawcare.presentation.components.products.ProductViewModel
import com.example.pawcare.presentation.register.PetRegisterScreen
import com.example.pawcare.presentation.screens.appointments.AppointmentConfirmationScreen
import com.example.pawcare.presentation.screens.appointments.ScheduleAppointmentScreen
import com.example.pawcare.presentation.screens.appointments.AppointmentListScreen
import com.example.pawcare.presentation.components.appointments.AppointmentViewModel
import com.example.pawcare.presentation.screens.product.InventoryListScreen
import com.example.pawcare.presentation.screens.product.ProductCatalogueScreen
import com.example.pawcare.presentation.screens.product.ProductFormScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.pawcare.presentation.screens.appointments.PaymentDetailScreen
import com.example.pawcare.presentation.screens.appointments.PaymentListScreen
import com.example.pawcare.presentation.screens.appointments.PaymentListViewModel
import com.example.pawcare.presentation.screens.appointments.PaymentScreen
import com.example.pawcare.presentation.screens.appointments.PaymentViewModel
import com.example.pawcare.presentation.screens.appointments.PaymentConfirmationScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    petRepository: PetRepository,
    paymentRepository: PaymentRepository
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
                onNavigateToAppointments = { navController.navigate(Screen.AppointmentList.route) },
                onNavigateToPaymentList = { navController.navigate(Screen.PaymentList.route) },
                onNavigateToPayment = { appointment ->
                    navController.navigate(
                        Screen.Payment.createRoute(
                            appointmentId = appointment.id,
                            petName = appointment.petName,
                            service = appointment.services.firstOrNull()?.name ?: "Servicio",
                            date = appointment.date,
                            amount = appointment.totalPrice,
                            employee = "Personal"
                        )
                    )
                },
                onNavigateToProduct = { navController.navigate(Screen.ProductList.route) }
            )
        }

        composable(
            route = Screen.ScheduleAppointment.route,
            arguments = listOf(
                navArgument("appointmentId") { 
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
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
                onNavigateToAppointments = { navController.navigate(Screen.AppointmentList.route) },
                onNavigateToPaymentList = { navController.navigate(Screen.PaymentList.route) }
            )
        }

        // --- PERFIL DE MASCOTA ---
        composable(
            route = Screen.AppointmentConfirmation.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("time") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType },
                navArgument("serviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val petId = backStackEntry.arguments?.getString("petId") ?: ""

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
        // --- PRODUCTOS (LISTA) ---
        composable(Screen.ProductList.route) {
            val viewModel: ProductViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            InventoryListScreen(
                state = state,
                onEvent = { event ->
                    when (event) {
                        is com.example.pawcare.presentation.components.products.ProductUiEvent.OnEditProductClick -> {
                            viewModel.onEvent(event)
                            navController.navigate("product_form?productId=${event.product.id}")
                        }
                        else -> viewModel.onEvent(event)
                    }
                },
                onNavigateToForm = {
                    viewModel.onEvent(com.example.pawcare.presentation.components.products.ProductUiEvent.OnClearForm)
                    navController.navigate("product_form")
                },
                onNavigateToCatalogue = {
                    navController.navigate(Screen.ProductCatalogue.route)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- PRODUCTOS (CATÁLOGO PREVIEW) ---
        composable(Screen.ProductCatalogue.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.ProductList.route)
            }
            val viewModel: ProductViewModel = hiltViewModel(parentEntry)
            val state by viewModel.state.collectAsState()

            ProductCatalogueScreen(
                state = state,
                onBack = { navController.popBackStack() }
            )
        }

        // --- PRODUCTOS (FORMULARIO) ---
        composable(
            route = "product_form?productId={productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ProductList.route)
            }
            val viewModel: ProductViewModel = hiltViewModel(parentEntry)
            val state by viewModel.state.collectAsState()
            ProductFormScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onBack = { navController.popBackStack() }
            )
        }

        // --- PAGO ---
        composable(
            route = Screen.Payment.route,
            arguments = listOf(
                navArgument("appointmentId") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType },
                navArgument("service") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("employee") { type = NavType.StringType }
            )
        ) {
            PaymentScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHome = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToPets = { navController.navigate(Screen.PetList.route) },
                onNavigateToProduct = { navController.navigate(Screen.ProductList.route) },
                onNavigateToAppointments = { navController.navigate(Screen.AppointmentList.route) },
                onNavigateToPaymentList = { navController.navigate(Screen.PaymentList.route) },
                onNavigateToConfirmation = { receipt, amount, pet, service, date, method, employee ->
                    navController.navigate(
                        Screen.PaymentConfirmation.createRoute(receipt, amount, pet, service, date, method, employee)
                    ) {
                        popUpTo(Screen.Payment.route) { inclusive = true }
                    }
                }
            )
        }

        // --- CONFIRMACIÓN DE PAGO ---
        composable(
            route = Screen.PaymentConfirmation.route,
            arguments = listOf(
                navArgument("receiptNumber") { type = NavType.StringType },
                navArgument("amount") { type = NavType.StringType },
                navArgument("petName") { type = NavType.StringType },
                navArgument("service") { type = NavType.StringType },
                navArgument("date") { type = NavType.StringType },
                navArgument("method") { type = NavType.StringType },
                navArgument("employee") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val receiptNumber = backStackEntry.arguments?.getString("receiptNumber") ?: ""
            val amountStr = backStackEntry.arguments?.getString("amount") ?: "0.0"
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val petName = backStackEntry.arguments?.getString("petName") ?: ""
            val service = backStackEntry.arguments?.getString("service") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            val method = backStackEntry.arguments?.getString("method") ?: ""
            val employee = backStackEntry.arguments?.getString("employee") ?: ""

            PaymentConfirmationScreen(
                receiptNumber = receiptNumber,
                amount = amount,
                petName = petName,
                service = service,
                date = date,
                method = method,
                employee = employee,
                onDownloadReceipt = { /* TODO */ },
                onViewPaymentList = {
                    navController.navigate(Screen.PaymentList.route) {
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

        // --- HISTORIAL DE COBROS ---
        composable(Screen.PaymentList.route) {
            val viewModel: PaymentListViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            PaymentListScreen(
                state = state,
                onEvent = viewModel::onEvent,
                onNavigateToDetail = { id -> navController.navigate(Screen.PaymentDetail.createRoute(id)) },
                onNavigateToHome = { navController.navigate(Screen.Home.route) },
                onNavigateToPets = { navController.navigate(Screen.PetList.route) },
                onNavigateToProducts = { navController.navigate(Screen.ProductList.route) },
                onNavigateToAppointments = { navController.navigate(Screen.AppointmentList.route) },
                onNavigateToPaymentList = { navController.navigate(Screen.PaymentList.route) }
            )
        }

        composable(
            route = Screen.PaymentDetail.route,
            arguments = listOf(navArgument("paymentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val paymentId = backStackEntry.arguments?.getString("paymentId") ?: ""
            PaymentDetailScreen(
                paymentId = paymentId,
                paymentRepository = paymentRepository,
                onBack = { navController.popBackStack() },
                isEdit = state.productId != null
                onDeleteSuccess = { navController.popBackStack() }
            )
        }
    }
}
