package com.example.pawcare.presentation.components.services

sealed interface ServiceUiEvent {
    object Refresh : ServiceUiEvent
}