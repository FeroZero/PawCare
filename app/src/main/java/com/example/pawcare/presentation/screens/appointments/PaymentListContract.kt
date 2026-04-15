package com.example.pawcare.presentation.screens.appointments

import com.example.pawcare.domain.model.Payment

data class PaymentListUiState(
    val payments: List<Payment> = emptyList(),
    val filteredPayments: List<Payment> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: PaymentFilter = PaymentFilter.ALL,
    val totalCount: Int = 0,
    val cardCount: Int = 0,
    val cashCount: Int = 0
)

enum class PaymentFilter {
    ALL, CARD, CASH
}

sealed class PaymentListUiEvent {
    object Refresh : PaymentListUiEvent()
    data class OnFilterSelected(val filter: PaymentFilter) : PaymentListUiEvent()
    data class OnDeletePayment(val paymentId: String) : PaymentListUiEvent()
}
