package com.example.pawcare.presentation.components.appointments

sealed interface AppointmentUiEvent {
    data class OnDateSelected(val date: String?) : AppointmentUiEvent
    object Refresh : AppointmentUiEvent

}