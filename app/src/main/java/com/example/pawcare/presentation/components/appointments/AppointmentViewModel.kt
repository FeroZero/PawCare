package com.example.pawcare.presentation.components.appointments

import androidx.activity.result.launch
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
        }
    }

    private fun loadAppointments(date: String? = null) {
        getAppointmentsUseCase(date = date).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> currentState.copy(
                        appointments = result.data,
                        isLoading = false,
                        error = null
                    )
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
        }.launchIn(viewModelScope)
    }

    private fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) } // Opcional: mostrar carga durante la actualización
            val result = updateAppointmentStatusUseCase(id, status, null)
            if (result is Resource.Error) {
                _state.update { it.copy(error = result.message, isLoading = false) }
            } else {
                _state.update { it.copy(isLoading = false, error = null) }
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
                _state.update { it.copy(isLoading = false, error = null) }
            }
        }
    }
}