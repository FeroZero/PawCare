package com.example.pawcare.presentation.components.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.appointments.GetAppointmentsUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AppointmentUiState())
    val state = _state.asStateFlow()

    init {
        loadAppointments()
    }

    fun onEvent(event: AppointmentUiEvent) {
        when (event) {
            is AppointmentUiEvent.OnDateSelected -> {
                _state.update { it.copy(selectedDate = event.date) }
                loadAppointments(event.date)
            }

            AppointmentUiEvent.Refresh -> loadAppointments(_state.value.selectedDate)
        }
    }

    private fun loadAppointments(date: String? = null) {
        getAppointmentsUseCase(date = date).onEach { result ->
            _state.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        currentState.copy(
                            appointments = result.data,
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        currentState.copy(
                            appointments = result.data ?: currentState.appointments,
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is Resource.Loading -> {
                        currentState.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}