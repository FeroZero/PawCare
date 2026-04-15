package com.example.pawcare.presentation.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.domain.repository.ServiceRepository
import com.example.pawcare.domain.use_case.appointments.CreateAppointmentUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ScheduleAppointmentViewModel @Inject constructor(
    private val petRepository: PetRepository,
    private val serviceRepository: ServiceRepository,
    private val createAppointmentUseCase: CreateAppointmentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleAppointmentUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ScheduleAppointmentSideEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadData()
        generateTimeSlots()
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }
        
        combine(
            petRepository.getPets(),
            serviceRepository.getServices()
        ) { petsResult, servicesResult ->
            _state.update { currentState ->
                currentState.copy(
                    pets = if (petsResult is Resource.Success) petsResult.data else emptyList(),
                    services = if (servicesResult is Resource.Success) servicesResult.data else emptyList(),
                    selectedPet = if (petsResult is Resource.Success) petsResult.data.firstOrNull() else null,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun generateTimeSlots() {
        val slots = listOf(
            TimeSlot("8:00 AM"),
            TimeSlot("9:00 AM"),
            TimeSlot("10:00 AM"),
            TimeSlot("11:00 AM"),
            TimeSlot("2:00 PM"),
            TimeSlot("3:00 PM"),
            TimeSlot("4:00 PM")
        )
        _state.update { it.copy(availableTimeSlots = slots) }
    }

    fun onEvent(event: ScheduleAppointmentUiEvent) {
        when (event) {
            is ScheduleAppointmentUiEvent.OnDateSelected -> {
                _state.update { it.copy(selectedDate = event.date) }
            }
            is ScheduleAppointmentUiEvent.OnTimeSlotSelected -> {
                _state.update { it.copy(selectedTimeSlot = event.timeSlot) }
            }
            is ScheduleAppointmentUiEvent.OnPetSelected -> {
                _state.update { it.copy(selectedPet = event.pet) }
            }
            is ScheduleAppointmentUiEvent.OnServiceSelected -> {
                _state.update { it.copy(selectedService = event.service) }
            }
            ScheduleAppointmentUiEvent.OnConfirmAppointment -> {
                confirmAppointment()
            }
            ScheduleAppointmentUiEvent.OnDismissSuccess -> {
                _state.update { it.copy(isSuccess = false) }
                viewModelScope.launch { _effect.emit(ScheduleAppointmentSideEffect.NavigateBack) }
            }
        }
    }

    private fun confirmAppointment() {
        val currentState = _state.value
        if (currentState.selectedTimeSlot == null || currentState.selectedPet == null || currentState.selectedService == null) {
            _state.update { it.copy(error = "Por favor completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateStr = currentState.selectedDate.format(formatter)
            
            val result = createAppointmentUseCase(
                date = dateStr,
                timeSlot = currentState.selectedTimeSlot,
                petId = currentState.selectedPet.id,
                serviceIds = listOf(currentState.selectedService.id),
                notes = null
            )

            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
