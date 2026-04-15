package com.example.pawcare.presentation.components.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.appointments.DeleteAppointmentUseCase
import com.example.pawcare.domain.use_case.appointments.GetAppointmentsUseCase
import com.example.pawcare.domain.use_case.appointments.UpdateAppointmentStatusUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val updateAppointmentStatusUseCase: UpdateAppointmentStatusUseCase,
    private val deleteAppointmentUseCase: DeleteAppointmentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AppointmentUiState())
    val state = _state.asStateFlow()

    init {
        loadAppointments()
    }

    fun onEvent(event: AppointmentUiEvent) {
        when (event) {
            is AppointmentUiEvent.OnDateSelected -> {
                _state.update { it.copy(selectedDate = event.date, error = null) }
                loadAppointments(event.date)
            }

            is AppointmentUiEvent.Refresh -> {
                _state.update { it.copy(error = null) }
                loadAppointments(_state.value.selectedDate)
            }

            is AppointmentUiEvent.OnDeleteAppointment -> {
                deleteAppointment(event.id)
            }

            is AppointmentUiEvent.OnStatusChange -> {
                updateStatus(event.id, event.newStatus)
            }
            
            is AppointmentUiEvent.OnFilterChanged -> {
                _state.update { it.copy(selectedFilter = event.filter) }
                applyFilter()
            }
        }
    }

    private fun loadAppointments(date: String? = null) {
        getAppointmentsUseCase(date = date).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        val appointments = result.data.sortedByDescending { it.date }
                        currentState.copy(
                            appointments = appointments,
                            isLoading = false,
                            error = null,
                            todayAppointmentsCount = appointments.size,
                            pendingCount = appointments.count { it.status.lowercase() == "pending" },
                            completedCount = appointments.count { it.status.lowercase() == "completed" },
                            cancelledCount = appointments.count { it.status.lowercase() == "cancelled" }
                        ).also { applyFilter() }
                    }
                    is Resource.Error -> currentState.copy(
                        appointments = result.data ?: currentState.appointments,
                        isLoading = false,
                        error = result.message
                    )
                    is Resource.Loading -> currentState.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }
            applyFilter()
        }.launchIn(viewModelScope)
    }

    private fun applyFilter() {
        _state.update { currentState ->
            val filtered = when (currentState.selectedFilter) {
                AppointmentFilter.ALL -> currentState.appointments
                AppointmentFilter.PENDING -> currentState.appointments.filter { it.status.lowercase() == "pending" }
                AppointmentFilter.COMPLETED -> currentState.appointments.filter { it.status.lowercase() == "completed" }
                AppointmentFilter.CANCELLED -> currentState.appointments.filter { it.status.lowercase() == "cancelled" }
            }
            currentState.copy(filteredAppointments = filtered)
        }
    }

    private fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = updateAppointmentStatusUseCase(id, status, null)
            if (result is Resource.Error) {
                _state.update { it.copy(error = result.message, isLoading = false) }
            } else {
                loadAppointments(_state.value.selectedDate)
            }
        }
    }

    private fun deleteAppointment(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = deleteAppointmentUseCase(id)
            if (result is Resource.Error) {
                _state.update { it.copy(error = result.message, isLoading = false) }
            } else {
                loadAppointments(_state.value.selectedDate)
            }
        }
    }
}
