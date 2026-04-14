package com.example.pawcare.presentation.components.services

sealed interface ServiceUiEvent {
    object Refresh : ServiceUiEvent
    data class OnSearchQueryChange(val query: String) : ServiceUiEvent
    data class OnDeleteService(val id: String) : ServiceUiEvent
}