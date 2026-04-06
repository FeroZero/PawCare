package com.example.pawcare.domain.use_case.appointments

import com.example.pawcare.domain.util.Resource
import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.repository.AppointmentRepository
import javax.inject.Inject

class CreateAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(
        date: String,
        timeSlot: String,
        petId: String,
        serviceIds: List<String>,
        notes: String?
    ): Resource<Appointment> = repository.createAppointment(date, timeSlot, petId, serviceIds, notes)
}