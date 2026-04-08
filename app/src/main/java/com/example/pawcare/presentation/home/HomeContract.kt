package com.example.pawcare.presentation.home

import com.example.pawcare.domain.model.Appointment

data class HomeState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userName: String = "Ana",
    val userInitials: String = "AL"
) {
    val todayAppointmentsCount = appointments.size
    val pendingCount = appointments.count { it.status.lowercase() == "pending" }
    val completedCount = appointments.count { it.status.lowercase() == "completed" }
}

sealed class HomeEvent {
    object Refresh : HomeEvent()
    data class OnQuickActionClick(val action: QuickAction) : HomeEvent()
}

enum class QuickAction {
    REGISTER_PET, SCHEDULE_APPOINTMENT, PET_LIST, BILLING
}

sealed class HomeEffect {
    object NavigateToRegisterPet : HomeEffect()
    object NavigateToPetList : HomeEffect()
    object NavigateToAppointments : HomeEffect()
    object NavigateToBilling : HomeEffect()
}
