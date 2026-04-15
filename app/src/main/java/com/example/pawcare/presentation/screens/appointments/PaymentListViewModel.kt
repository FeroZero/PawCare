package com.example.pawcare.presentation.screens.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.repository.PaymentRepository
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentListViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentListUiState())
    val state = _state.asStateFlow()

    init {
        observePayments()
    }

    private fun observePayments() {
        paymentRepository.getPayments().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    val payments = result.data
                    _state.update { it.copy(
                        payments = payments,
                        isLoading = false,
                        totalCount = payments.size,
                        cardCount = payments.count { p -> p.paymentMethod.lowercase() == "tarjeta" },
                        cashCount = payments.count { p -> p.paymentMethod.lowercase() == "efectivo" }
                    ) }
                    applyFilter()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: PaymentListUiEvent) {
        when (event) {
            PaymentListUiEvent.Refresh -> observePayments()
            is PaymentListUiEvent.OnFilterSelected -> {
                _state.update { it.copy(selectedFilter = event.filter) }
                applyFilter()
            }
            is PaymentListUiEvent.OnDeletePayment -> deletePayment(event.paymentId)
        }
    }

    private fun applyFilter() {
        _state.update { currentState ->
            val filtered = when (currentState.selectedFilter) {
                PaymentFilter.ALL -> currentState.payments
                PaymentFilter.CARD -> currentState.payments.filter { it.paymentMethod.lowercase() == "tarjeta" }
                PaymentFilter.CASH -> currentState.payments.filter { it.paymentMethod.lowercase() == "efectivo" }
            }
            currentState.copy(filteredPayments = filtered)
        }
    }

    private fun deletePayment(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            paymentRepository.deletePayment(id)
            // observePayments handles the UI update since it's observing Room Flow
        }
    }
}
