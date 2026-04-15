package com.example.pawcare.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.model.Service
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    onNavigateToRegisterPet: () -> Unit,
    onNavigateToPetList: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToPaymentList: () -> Unit,
    onNavigateToPayment: (Appointment) -> Unit,
    onNavigateToProduct: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                HomeEffect.NavigateToRegisterPet -> onNavigateToRegisterPet()
                HomeEffect.NavigateToPetList -> onNavigateToPetList()
                HomeEffect.NavigateToAppointments -> onNavigateToAppointments()
                HomeEffect.NavigateToPaymentList -> onNavigateToPaymentList()
                HomeEffect.NavigateToProduct -> onNavigateToProduct()
                is HomeEffect.NavigateToPayment -> onNavigateToPayment(effect.appointment)
                else -> {}
            }
        }
    }

    if (state.isSelectAppointmentDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(HomeEvent.OnDismissDialog) },
            confirmButton = {},
            title = { Text("Selecciona mascota para cobrar", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.pendingAppointments) { appointment ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEvent(HomeEvent.OnAppointmentSelected(appointment)) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF9F5)),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pets, null, tint = Color(0xFF3D2314), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(appointment.petName, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = appointment.services.joinToString(", ") { it.name }.ifEmpty { "Servicio" },
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "RD$${appointment.totalPrice}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF3D2314),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    if (state.isServicesDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(HomeEvent.OnDismissServicesDialog) },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(HomeEvent.OnDismissServicesDialog) }) {
                    Text("Cerrar", color = Color(0xFF3D2314))
                }
            },
            title = { Text("Servicios y Precios", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.services) { service ->
                        ServicePriceItem(service)
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        bottomBar = { 
            PawBottomBar(
                onNavigateToHome = { /* Already here */ },
                onNavigateToPets = onNavigateToPetList,
                onNavigateToProduct = onNavigateToProduct,
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToPaymentList = onNavigateToPaymentList,
                currentRoute = "home"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5EFE6))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola, ${state.userName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.userInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Citas de hoy",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = state.todayAppointmentsCount.toString(),
                        color = Color.White,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        SummaryStat(label = "Pendientes", value = state.pendingCount.toString())
                        Spacer(modifier = Modifier.width(32.dp))
                        SummaryStat(label = "Completadas", value = state.completedCount.toString())
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Próximas citas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.appointments.take(3)) { appointment ->
                    AppointmentItem(appointment)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Acceso rápido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    QuickActionsGrid(onActionClick = { viewModel.onEvent(HomeEvent.OnQuickActionClick(it)) })
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ServicePriceItem(service: Service) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF9F5)),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Bold, color = Color(0xFF3D2314))
                Text(service.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Text(
                text = "RD$${service.price}",
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF3D2314),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun SummaryStat(label: String, value: String) {
    Column {
        Text(text = value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun AppointmentItem(appointment: Appointment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appointment.petName.take(2).uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = appointment.petName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = appointment.services.joinToString(", ") { it.name }.ifEmpty { "Sin servicios" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total: RD$${appointment.totalPrice}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF3D2314),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = appointment.timeSlot,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun QuickActionsGrid(onActionClick: (QuickAction) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionButton(
            icon = Icons.Default.Pets,
            label = "Mascotas",
            modifier = Modifier.weight(1f),
            onClick = { onActionClick(QuickAction.PET_LIST) }
        )
        QuickActionButton(
            icon = Icons.Default.AttachMoney,
            label = "Cobrar",
            modifier = Modifier.weight(1f),
            onClick = { onActionClick(QuickAction.BILLING) }
        )
        QuickActionButton(
            icon = Icons.AutoMirrored.Filled.ListAlt,
            label = "Servicios",
            modifier = Modifier.weight(1f),
            onClick = { onActionClick(QuickAction.SERVICES) }
        )
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun PawBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToPets: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToPaymentList: () -> Unit,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            selected = currentRoute == "home",
            onClick = onNavigateToHome
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Pets, contentDescription = null) },
            label = { Text("Mascotas") },
            selected = currentRoute == "pet_list",
            onClick = onNavigateToPets
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory2, contentDescription = null) },
            label = { Text("Inventario") },
            selected = currentRoute == "product_list",
            onClick = onNavigateToProduct
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Payments, contentDescription = null) },
            label = { Text("Cobros") },
            selected = currentRoute == "payment_list",
            onClick = onNavigateToPaymentList
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Citas") },
            selected = currentRoute == "appointment_list",
            onClick = onNavigateToAppointments
        )
    }
}
