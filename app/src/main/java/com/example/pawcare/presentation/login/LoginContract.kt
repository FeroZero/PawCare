package com.example.pawcare.presentation.login

data class LoginState(
    val profiles: List<EmployeeProfile> = listOf(
        EmployeeProfile("1", "Ana Lopez", "Groomer Senior", "AL", 0xFF5C3D2E),
        EmployeeProfile("2", "Maria Reyes", "Recepcionista", "MR", 0xFFA07850),
        EmployeeProfile("3", "Carlos Garcia", "Asistente", "CG", 0xFF436B4E)
    ),
    val selectedProfileId: String? = null,
    val isLoading: Boolean = false
)

data class EmployeeProfile(
    val id: String,
    val name: String,
    val role: String,
    val initials: String,
    val color: Long
)

sealed class LoginEvent {
    data class SelectProfile(val id: String) : LoginEvent()
    object EnterSystem : LoginEvent()
}

sealed class LoginEffect {
    object NavigateToHome : LoginEffect()
}
