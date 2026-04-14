package com.example.pawcare.presentation.components.appointments

sealed interface AppointmentUiEvent {
    data class OnDateSelected(val date: String?) : AppointmentUiEvent
    object Refresh : AppointmentUiEvent

    data class OnDeleteAppointment(val id: String) : AppointmentUiEvent

    data class OnStatusChange(val id: String, val newStatus: String) : AppointmentUiEvent
}
