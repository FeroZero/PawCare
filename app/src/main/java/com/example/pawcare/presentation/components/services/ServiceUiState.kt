package com.example.pawcare.presentation.components.services

import com.example.pawcare.domain.model.Service

data class ServiceUiState(
    val services: List<Service> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)