package com.example.pawcare.domain.use_case.appointments

import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.repository.AppointmentRepository
import com.example.pawcare.domain.util.Resource
import javax.inject.Inject

class UpdateAppointmentStatusUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(
        id: String,
        status: String,
        paymentMethod: String?
    ): Resource<Appointment> = repository.updateAppointmentStatus(id, status, paymentMethod)
}