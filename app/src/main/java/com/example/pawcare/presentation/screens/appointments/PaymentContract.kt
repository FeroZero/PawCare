package com.example.pawcare.presentation.screens.appointments

data class PaymentUiState(
    val appointmentId: String = "",
    val petName: String = "",
    val serviceName: String = "",
    val date: String = "",
    val amount: Double = 0.0,
    val employeeName: String = "",
    val paymentMethod: PaymentMethod? = null,
    val cardNumber: String = "",
    val expiration: String = "",
    val cvv: String = "",
    val cashReceived: String = "",
    val receiptNumber: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

enum class PaymentMethod {
    CARD, CASH
}

sealed class PaymentUiEvent {
    data class OnMethodSelected(val method: PaymentMethod) : PaymentUiEvent()
    data class OnCardNumberChanged(val value: String) : PaymentUiEvent()
    data class OnExpirationChanged(val value: String) : PaymentUiEvent()
    data class OnCvvChanged(val value: String) : PaymentUiEvent()
    data class OnCashChanged(val value: String) : PaymentUiEvent()
    object OnConfirmPayment : PaymentUiEvent()
}

sealed class PaymentSideEffect {
    data class NavigateToConfirmation(
        val receiptNumber: String,
        val amount: Double,
        val petName: String,
        val service: String,
        val date: String,
        val method: String,
        val employee: String
    ) : PaymentSideEffect()
}
