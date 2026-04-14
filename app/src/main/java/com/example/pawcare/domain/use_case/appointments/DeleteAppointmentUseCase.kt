package com.example.pawcare.domain.use_case.appointments

import com.example.pawcare.domain.repository.AppointmentRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class DeleteAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.deleteAppointment(id)
    }
}