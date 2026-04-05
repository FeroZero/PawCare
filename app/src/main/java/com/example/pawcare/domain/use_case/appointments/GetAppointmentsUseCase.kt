package com.example.pawcare.domain.use_case.appointments

import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.repository.AppointmentRepository
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    operator fun invoke(date: String? = null, status: String? = null): Flow<Resource<List<Appointment>>> =
        repository.getAppointments(date, status)
}
