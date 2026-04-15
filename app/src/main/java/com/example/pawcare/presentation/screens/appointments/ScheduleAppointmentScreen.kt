package com.example.pawcare.presentation.screens.appointments

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.model.Service
import com.example.pawcare.presentation.home.PawBottomBar
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleAppointmentScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegisterPet: () -> Unit,
    onNavigateToConfirmation: (String, String, String, String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPets: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    viewModel: ScheduleAppointmentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToConfirmation(
                state.selectedDate.toString(),
                state.selectedTimeSlot ?: "",
                state.selectedPet?.name ?: "",
                state.selectedService?.name ?: ""
            )
            viewModel.onEvent(ScheduleAppointmentUiEvent.OnDismissSuccess)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agendar Cita", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFFF5EFE6))
            )
        },
        bottomBar = { 
            PawBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToPets = onNavigateToPets,
                onNavigateToProduct = onNavigateToProduct,
                onNavigateToAppointments = onNavigateToAppointments,
                currentRoute = "schedule_appointment"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5EFE6)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            CalendarSection(
                selectedDate = state.selectedDate,
                onDateSelected = { viewModel.onEvent(ScheduleAppointmentUiEvent.OnDateSelected(it)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Horario disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(140.dp)
            ) {
                items(state.availableTimeSlots) { slot ->
                    TimeSlotChip(
                        time = slot.time,
                        isSelected = state.selectedTimeSlot == slot.time,
                        isAvailable = slot.isAvailable,
                        onClick = { viewModel.onEvent(ScheduleAppointmentUiEvent.OnTimeSlotSelected(slot.time)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("SELECCIONAR PERRO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            PetDropdown(
                selectedPet = state.selectedPet,
                pets = state.pets,
                onPetSelected = { viewModel.onEvent(ScheduleAppointmentUiEvent.OnPetSelected(it)) },
                onAddPet = onNavigateToRegisterPet
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("SELECCIONAR SERVICIO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            ServiceDropdown(
                selectedService = state.selectedService,
                services = state.services,
                onServiceSelected = { viewModel.onEvent(ScheduleAppointmentUiEvent.OnServiceSelected(it)) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(ScheduleAppointmentUiEvent.OnConfirmAppointment) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2314)),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirmar Cita", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun CalendarSection(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val monthYear = "${selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es")).uppercase()} ${selectedDate.year}"
    
    Column {
        Text(text = monthYear, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")
            daysOfWeek.forEach { day ->
                Text(text = day, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val startDate = LocalDate.now().minusDays(2)
            items(14) { index ->
                val date = startDate.plusDays(index.toLong())
                val isSelected = date == selectedDate
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) Color(0xFF3D2314) else Color.Transparent)
                        .clickable { onDateSelected(date) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else Color.Gray.copy(alpha = 0.3f)))
                }
            }
        }
    }
}

@Composable
fun TimeSlotChip(time: String, isSelected: Boolean, isAvailable: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = when {
            isSelected -> Color(0xFF3D2314)
            !isAvailable -> Color.LightGray.copy(alpha = 0.4f)
            else -> Color.White
        },
        border = if (!isSelected && isAvailable) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
    ) {
        Text(
            text = time,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            color = if (isSelected) Color.White else if (!isAvailable) Color.Gray else Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PetDropdown(selectedPet: Pet?, pets: List<Pet>, onPetSelected: (Pet) -> Unit, onAddPet: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Pets, contentDescription = null, tint = Color(0xFF3D2314), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = selectedPet?.name ?: "Seleccionar mascota", modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
        ) {
            pets.forEach { pet ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(pet.name)
                        }
                    },
                    onClick = {
                        onPetSelected(pet)
                        expanded = false
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar nueva mascota", color = Color(0xFF3D2314), fontWeight = FontWeight.Bold)
                    }
                },
                onClick = {
                    onAddPet()
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun ServiceDropdown(selectedService: Service?, services: List<Service>, onServiceSelected: (Service) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ContentCut, contentDescription = null, tint = Color(0xFF3D2314), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = selectedService?.name ?: "Seleccionar servicio", modifier = Modifier.weight(1f))
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f).background(Color.White)
        ) {
            services.forEach { service ->
                val icon = when {
                    service.name.lowercase().contains("baño") && service.name.lowercase().contains("corte") -> Icons.Default.AutoFixHigh
                    service.name.lowercase().contains("baño") -> Icons.Default.WaterDrop
                    service.name.lowercase().contains("corte") -> Icons.Default.ContentCut
                    service.name.lowercase().contains("revisión") -> Icons.Default.HealthAndSafety
                    service.name.lowercase().contains("vacunación") -> Icons.Default.Vaccines
                    else -> Icons.Default.SettingsSuggest
                }
                
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(service.name)
                        }
                    },
                    onClick = {
                        onServiceSelected(service)
                        expanded = false
                    }
                )
            }
        }
    }
}
