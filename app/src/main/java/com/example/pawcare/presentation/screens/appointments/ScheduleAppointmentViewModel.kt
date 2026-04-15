package com.example.pawcare.presentation.screens.appointments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.repository.AppointmentRepository
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
    private val appointmentRepository: AppointmentRepository,
    private val createAppointmentUseCase: CreateAppointmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(ScheduleAppointmentUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ScheduleAppointmentSideEffect>()
    val effect = _effect.asSharedFlow()

    private val appointmentId: String? = savedStateHandle["appointmentId"]
    private var isEditLoaded = false

    init {
        loadMasterData()
        generateTimeSlots()
    }

    private fun loadMasterData() {
        _state.update { it.copy(isLoading = true) }
        combine(
            petRepository.getPets(),
            serviceRepository.getServices()
        ) { petsResult, servicesResult ->
            val pets = if (petsResult is Resource.Success) petsResult.data else emptyList()
            val services = if (servicesResult is Resource.Success) servicesResult.data else emptyList()
            
            _state.update { currentState ->
                currentState.copy(
                    pets = pets,
                    services = services,
                    selectedPet = currentState.selectedPet ?: pets.firstOrNull(),
                    selectedService = currentState.selectedService ?: services.firstOrNull(),
                    isLoading = false
                )
            }
            
            // Cargamos la cita solo una vez para evitar sobrescribir cambios del usuario
            if (appointmentId != null && !isEditLoaded && pets.isNotEmpty() && services.isNotEmpty()) {
                loadAppointmentForEdit(appointmentId)
            }
        }.launchIn(viewModelScope)
    }

    private fun loadAppointmentForEdit(id: String) {
        isEditLoaded = true
        viewModelScope.launch {
            val result = appointmentRepository.getAppointmentById(id)
            if (result is Resource.Success) {
                val appointment = result.data
                _state.update { currentState ->
                    currentState.copy(
                        selectedDate = try { LocalDate.parse(appointment.date) } catch(e: Exception) { LocalDate.now() },
                        selectedTimeSlot = appointment.timeSlot,
                        selectedPet = currentState.pets.find { it.id == appointment.petId },
                        selectedService = currentState.services.find { s -> appointment.services.any { it.id == s.id } }
                    )
                }
            }
        }
    }

    private fun generateTimeSlots() {
        val slots = listOf(
            TimeSlot("8:00 AM"), TimeSlot("9:00 AM"), TimeSlot("10:00 AM"),
            TimeSlot("11:00 AM"), TimeSlot("2:00 PM"), TimeSlot("3:00 PM"),
            TimeSlot("4:00 PM")
        )
        _state.update { it.copy(availableTimeSlots = slots) }
    }

    fun onEvent(event: ScheduleAppointmentUiEvent) {
        when (event) {
            is ScheduleAppointmentUiEvent.OnDateSelected -> _state.update { it.copy(selectedDate = event.date) }
            is ScheduleAppointmentUiEvent.OnTimeSlotSelected -> _state.update { it.copy(selectedTimeSlot = event.timeSlot) }
            is ScheduleAppointmentUiEvent.OnPetSelected -> _state.update { it.copy(selectedPet = event.pet) }
            is ScheduleAppointmentUiEvent.OnServiceSelected -> _state.update { it.copy(selectedService = event.service) }
            ScheduleAppointmentUiEvent.OnConfirmAppointment -> confirmAppointment()
            ScheduleAppointmentUiEvent.OnDismissSuccess -> {
                _state.update { it.copy(isSuccess = false) }
                viewModelScope.launch { _effect.emit(ScheduleAppointmentSideEffect.NavigateBack) }
            }
        }
    }

    private fun confirmAppointment() {
        val currentState = _state.value
        if (currentState.selectedTimeSlot == null || currentState.selectedPet == null || currentState.selectedService == null) {
            _state.update { it.copy(error = "Completa todos los campos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val dateStr = currentState.selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            
            val result = if (appointmentId == null) {
                createAppointmentUseCase(dateStr, currentState.selectedTimeSlot, currentState.selectedPet.id, listOf(currentState.selectedService.id), null)
            } else {
                appointmentRepository.updateAppointment(com.example.pawcare.domain.model.Appointment(
                    id = appointmentId,
                    date = dateStr,
                    timeSlot = currentState.selectedTimeSlot,
                    status = "pending",
                    totalPrice = currentState.selectedService.price,
                    paymentMethod = null,
                    notes = null,
                    petId = currentState.selectedPet.id,
                    petName = currentState.selectedPet.name,
                    petPhotoUrl = null,
                    services = listOf(currentState.selectedService)
                ))
            }

            if (result is Resource.Success) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } else if (result is Resource.Error) {
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
        }
    }
}
