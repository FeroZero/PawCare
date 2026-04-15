package com.example.pawcare.presentation.screens.appointments

import com.example.pawcare.domain.model.Pet
import com.example.pawcare.domain.model.Service
import java.time.LocalDate

data class ScheduleAppointmentUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTimeSlot: String? = null,
    val selectedPet: Pet? = null,
    val selectedService: Service? = null,
    val pets: List<Pet> = emptyList(),
    val services: List<Service> = emptyList(),
    val availableTimeSlots: List<TimeSlot> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

data class TimeSlot(
    val time: String,
    val isAvailable: Boolean = true
)

sealed class ScheduleAppointmentUiEvent {
    data class OnDateSelected(val date: LocalDate) : ScheduleAppointmentUiEvent()
    data class OnTimeSlotSelected(val timeSlot: String) : ScheduleAppointmentUiEvent()
    data class OnPetSelected(val pet: Pet) : ScheduleAppointmentUiEvent()
    data class OnServiceSelected(val service: Service) : ScheduleAppointmentUiEvent()
    object OnConfirmAppointment : ScheduleAppointmentUiEvent()
    object OnDismissSuccess : ScheduleAppointmentUiEvent()
}

sealed class ScheduleAppointmentSideEffect {
    object NavigateBack : ScheduleAppointmentSideEffect()
    object NavigateToRegisterPet : ScheduleAppointmentSideEffect()
}
