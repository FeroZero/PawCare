package com.example.pawcare.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.use_case.appointments.GetAppointmentsUseCase
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAppointmentsUseCase: GetAppointmentsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadAppointments()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Refresh -> loadAppointments()
            is HomeEvent.OnQuickActionClick -> {
                viewModelScope.launch {
                    when (event.action) {
                        QuickAction.REGISTER_PET -> _effect.emit(HomeEffect.NavigateToRegisterPet)
                        QuickAction.SCHEDULE_APPOINTMENT -> _effect.emit(HomeEffect.NavigateToAppointments)
                        QuickAction.PET_LIST -> _effect.emit(HomeEffect.NavigateToPetList)
                        QuickAction.BILLING -> _effect.emit(HomeEffect.NavigateToBilling)
                    }
                }
            }
        }
    }

    private fun loadAppointments() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        getAppointmentsUseCase(date = today).onEach { result ->
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
}
