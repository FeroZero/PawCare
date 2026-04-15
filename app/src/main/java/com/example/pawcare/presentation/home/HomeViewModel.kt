package com.example.pawcare.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.appointments.GetAppointmentsUseCase
import com.example.pawcare.domain.use_case.services.GetServicesUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        _state.update { it.copy(todayDate = today) }
        loadAppointments()
        loadServices()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> {
                loadAppointments()
                loadServices()
            }
            is HomeEvent.OnQuickActionClick -> {
                viewModelScope.launch {
                    when (event.action) {
                        QuickAction.REGISTER_PET -> _effect.emit(HomeEffect.NavigateToRegisterPet)
                        QuickAction.SCHEDULE_APPOINTMENT -> _effect.emit(HomeEffect.NavigateToAppointments)
                        QuickAction.PET_LIST -> _effect.emit(HomeEffect.NavigateToPetList)
                        QuickAction.BILLING -> {
                            if (_state.value.pendingAppointments.isEmpty()) {
                                _effect.emit(HomeEffect.NavigateToPaymentList)
                            } else {
                                _state.update { it.copy(isSelectAppointmentDialogOpen = true) }
                            }
                        }
                        QuickAction.PRODUCT ->  _effect.emit(HomeEffect.NavigateToProduct)
                        QuickAction.SERVICES -> _state.update { it.copy(isServicesDialogOpen = true) }
                    }
                }
            }
            HomeEvent.OnDismissDialog -> {
                _state.update { it.copy(isSelectAppointmentDialogOpen = false) }
            }
            HomeEvent.OnDismissServicesDialog -> {
                _state.update { it.copy(isServicesDialogOpen = false) }
            }
            is HomeEvent.OnAppointmentSelected -> {
                _state.update { it.copy(isSelectAppointmentDialogOpen = false) }
                viewModelScope.launch {
                    _effect.emit(HomeEffect.NavigateToPayment(event.appointment))
                }
            }
        }
    }

    private fun loadAppointments() {
        getAppointmentsUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.update { it.copy(
                        appointments = result.data,
                        isLoading = false,
                        error = null
                    ) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(
                        isLoading = false,
                        error = result.message
                    ) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun loadServices() {
        getServicesUseCase().onEach { result ->
            if (result is Resource.Success) {
                _state.update { it.copy(services = result.data) }
            }
        }.launchIn(viewModelScope)
    }
}
