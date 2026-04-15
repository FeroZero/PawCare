package com.example.pawcare.presentation.components.appointments

import com.example.pawcare.domain.model.Appointment

data class AppointmentUiState(
    val appointments: List<Appointment> = emptyList(),
    val filteredAppointments: List<Appointment> = emptyList(),
    val todayAppointmentsCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val cancelledCount: Int = 0,

    val selectedFilter: AppointmentFilter = AppointmentFilter.ALL,
    val selectedDate: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class AppointmentFilter {
    ALL, PENDING, COMPLETED, CANCELLED
}
