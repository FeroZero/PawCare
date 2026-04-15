package com.example.pawcare.presentation.screens.appointments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.presentation.components.appointments.AppointmentFilter
import com.example.pawcare.presentation.components.appointments.AppointmentUiEvent
import com.example.pawcare.presentation.components.appointments.AppointmentUiState
import com.example.pawcare.presentation.home.PawBottomBar
import com.example.pawcare.ui.theme.Background
import com.example.pawcare.ui.theme.TextPrimary

@Composable
fun AppointmentListScreen(
    state: AppointmentUiState,
    onEvent: (AppointmentUiEvent) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPets: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToPaymentList: () -> Unit,
    onNavigateToAppointments: () -> Unit
) {
    var appointmentToDelete by remember { mutableStateOf<Appointment?>(null) }

    if (appointmentToDelete != null) {
        AlertDialog(
            onDismissRequest = { appointmentToDelete = null },
            title = { Text("¿Eliminar cita?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer. ¿Deseas eliminar la cita de ${appointmentToDelete?.petName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        appointmentToDelete?.let { onEvent(AppointmentUiEvent.OnDeleteAppointment(it.id)) }
                        appointmentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { appointmentToDelete = null }) {
                    Text("No, mantener", color = Color(0xFF3D2314))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSchedule,
                containerColor = Color(0xFF3D2314),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agendar Cita")
            }
        },
        bottomBar = { 
            PawBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToPets = onNavigateToPets,
                onNavigateToProduct = onNavigateToProducts,
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToPaymentList = onNavigateToPaymentList,
                currentRoute = "appointment_list"
            ) 
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Mis Citas",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D2314)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { FilterChipItem("Todas", state.appointments.size, state.selectedFilter == AppointmentFilter.ALL) { onEvent(AppointmentUiEvent.OnFilterChanged(AppointmentFilter.ALL)) } }
                item { FilterChipItem("Pendientes", state.pendingCount, state.selectedFilter == AppointmentFilter.PENDING) { onEvent(AppointmentUiEvent.OnFilterChanged(AppointmentFilter.PENDING)) } }
                item { FilterChipItem("Completadas", state.completedCount, state.selectedFilter == AppointmentFilter.COMPLETED) { onEvent(AppointmentUiEvent.OnFilterChanged(AppointmentFilter.COMPLETED)) } }
                item { FilterChipItem("Canceladas", state.cancelledCount, state.selectedFilter == AppointmentFilter.CANCELLED) { onEvent(AppointmentUiEvent.OnFilterChanged(AppointmentFilter.CANCELLED)) } }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.filteredAppointments.isEmpty() && !state.isLoading) {
                EmptyAppointmentsView(onNavigateToSchedule)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.filteredAppointments) { appointment ->
                        AppointmentCard(
                            appointment = appointment,
                            onEdit = { onNavigateToEdit(appointment.id) },
                            onDelete = { appointmentToDelete = appointment }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipItem(label: String, count: Int, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text("$label ($count)") },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF3D2314),
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color.Gray
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Color.Transparent else Color(0xFFE2D9CF)
        )
    )
}

@Composable
fun AppointmentCard(appointment: Appointment, onEdit: () -> Unit, onDelete: () -> Unit) {
    val statusColor = when (appointment.status.lowercase()) {
        "pending", "pendiente" -> Color(0xFFFFF9C4) to Color(0xFFFBC02D)
        "completed", "completada" -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        else -> Color(0xFFFFEBEE) to Color(0xFFEF5350)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Pets, null, tint = Color(0xFF3D2314), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(appointment.petName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                
                Surface(
                    color = statusColor.first,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = appointment.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = statusColor.second,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            AppointmentInfoRow(Icons.Default.ContentCut, appointment.services.joinToString(", ") { it.name })
            AppointmentInfoRow(Icons.Default.Event, appointment.date)
            AppointmentInfoRow(Icons.Default.AccessTime, appointment.timeSlot)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color(0xFFF0ECE7))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", color = Color(0xFF3D2314))
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar", color = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}

@Composable
fun AppointmentInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = TextPrimary, fontSize = 14.sp)
    }
}

@Composable
fun EmptyAppointmentsView(onSchedule: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
        Spacer(modifier = Modifier.height(24.dp))
        Text("No tienes citas agendadas", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
        Text("¡Agenda una cita para tu mascota!", color = Color.Gray, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onSchedule,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2314)),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Agendar ahora")
        }
    }
}
