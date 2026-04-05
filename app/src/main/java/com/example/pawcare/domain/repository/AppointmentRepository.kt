package com.example.pawcare.domain.repository

import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    fun getAppointments(date: String?, status: String?): Flow<Resource<List<Appointment>>>
    suspend fun createAppointment(
        date: String,
        timeSlot: String,
        petId: String,
        serviceIds: List<String>,
        notes: String?
    ): Resource<Appointment>
    suspend fun updateAppointmentStatus(id: String, status: String, paymentMethod: String?): Resource<Appointment>
}
