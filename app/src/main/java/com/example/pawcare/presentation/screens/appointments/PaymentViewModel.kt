package com.example.pawcare.presentation.screens.appointments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pawcare.domain.model.Payment
import com.example.pawcare.domain.repository.PaymentRepository
import com.example.pawcare.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentUiState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PaymentSideEffect>()
    val effect = _effect.asSharedFlow()

    init {
        val appointmentId: String = savedStateHandle.get<String>("appointmentId") ?: ""
        val petName: String = savedStateHandle.get<String>("petName") ?: ""
        val service: String = savedStateHandle.get<String>("service") ?: ""
        val date: String = savedStateHandle.get<String>("date") ?: ""
        val amountStr: String = savedStateHandle.get<String>("amount") ?: "0.0"
        val amount: Double = amountStr.toDoubleOrNull() ?: 0.0
        val employee: String = savedStateHandle.get<String>("employee") ?: ""

        viewModelScope.launch {
            val nextReceipt = paymentRepository.getNextReceiptNumber()
            _state.update { it.copy(
                appointmentId = appointmentId,
                petName = petName,
                serviceName = service,
                date = date,
                amount = amount,
                employeeName = employee,
                receiptNumber = nextReceipt
            ) }
        }
    }

    fun onEvent(event: PaymentUiEvent) {
        when (event) {
            is PaymentUiEvent.OnMethodSelected -> _state.update { it.copy(paymentMethod = event.method, error = null) }
            is PaymentUiEvent.OnCardNumberChanged -> _state.update { it.copy(cardNumber = event.value) }
            is PaymentUiEvent.OnExpirationChanged -> _state.update { it.copy(expiration = event.value) }
            is PaymentUiEvent.OnCvvChanged -> _state.update { it.copy(cvv = event.value) }
            is PaymentUiEvent.OnCashChanged -> _state.update { it.copy(cashReceived = event.value) }
            PaymentUiEvent.OnConfirmPayment -> confirmPayment()
        }
    }

    private fun confirmPayment() {
        val currentState = _state.value
        if (currentState.paymentMethod == null) {
            _state.update { it.copy(error = "Selecciona un método de pago") }
            return
        }

        if (currentState.paymentMethod == PaymentMethod.CARD) {
            if (currentState.cardNumber.isBlank() || currentState.expiration.isBlank() || currentState.cvv.isBlank()) {
                _state.update { it.copy(error = "Completa los datos de la tarjeta") }
                return
            }
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val paymentMethodStr = when(currentState.paymentMethod) {
                PaymentMethod.CARD -> "Tarjeta"
                PaymentMethod.CASH -> "Efectivo"
            }

            val now = Date()
            val payment = Payment(
                id = UUID.randomUUID().toString(),
                appointmentId = currentState.appointmentId,
                petName = currentState.petName,
                serviceName = currentState.serviceName,
                amount = currentState.amount,
                paymentMethod = paymentMethodStr,
                appointmentDate = currentState.date,
                paymentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(now),
                paymentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now),
                employeeName = currentState.employeeName,
                receiptNumber = currentState.receiptNumber,
                status = "Pagado"
            )

            val result = paymentRepository.savePayment(payment)
            if (result is Resource.Success) {
                _state.update { it.copy(isLoading = false, isSuccess = true) }
                _effect.emit(
                    PaymentSideEffect.NavigateToConfirmation(
                        receiptNumber = payment.receiptNumber,
                        amount = payment.amount,
                        petName = payment.petName,
                        service = payment.serviceName,
                        date = "${payment.paymentDate} — ${payment.paymentTime}",
                        method = payment.paymentMethod,
                        employee = payment.employeeName
                    )
                )
            } else {
                _state.update { it.copy(isLoading = false, error = "Error al registrar el cobro") }
            }
        }
    }
}
