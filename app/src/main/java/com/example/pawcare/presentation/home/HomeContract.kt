package com.example.pawcare.presentation.home

import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.model.Service
import java.time.LocalDate

data class HomeState(
    val appointments: List<Appointment> = emptyList(),
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "Ana",
    val userInitials: String = "AL",
    val isSelectAppointmentDialogOpen: Boolean = false,
    val isServicesDialogOpen: Boolean = false,
    val todayDate: String = ""
) {
    // Calculamos estadísticas basadas en la fecha de hoy
    val todayAppointmentsCount = appointments.count { it.date == todayDate }
    val pendingCount = appointments.count { (it.status.lowercase().contains("pendient") || it.status.lowercase() == "pending") && it.date == todayDate }
    val completedCount = appointments.count { (it.status.lowercase().contains("complet") || it.status.lowercase() == "completed") && it.date == todayDate }
    
    // Lista de citas pendientes (de cualquier fecha) para cobrar
    val pendingAppointments = appointments.filter { it.status.lowercase().contains("pendient") || it.status.lowercase() == "pending" }
}

sealed class HomeEvent {
    object Refresh : HomeEvent()
    data class OnQuickActionClick(val action: QuickAction) : HomeEvent()
    object OnDismissDialog : HomeEvent()
    data class OnAppointmentSelected(val appointment: Appointment) : HomeEvent()
    object OnDismissServicesDialog : HomeEvent()
}

enum class QuickAction {
    REGISTER_PET, SCHEDULE_APPOINTMENT, PET_LIST, BILLING, PRODUCT, SERVICES
}

sealed class HomeEffect {
    object NavigateToRegisterPet : HomeEffect()
    object NavigateToPetList : HomeEffect()
    object NavigateToAppointments : HomeEffect()
    object NavigateToPaymentList : HomeEffect()
    object NavigateToProduct : HomeEffect()
    data class NavigateToPayment(val appointment: Appointment) : HomeEffect()
}
