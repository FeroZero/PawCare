package com.example.pawcare.data.repository

import com.example.pawcare.data.local.dao.AppointmentDao
import com.example.pawcare.data.mapper.toAppointment
import com.example.pawcare.data.mapper.toEntity
import com.example.pawcare.data.remote.PawCareApiService
import com.example.pawcare.data.remote.SafeApiCall
import com.example.pawcare.data.remote.dto.CreateAppointmentRequest
import com.example.pawcare.data.remote.dto.UpdateAppointmentStatusRequest
import com.example.pawcare.domain.model.Appointment
import com.example.pawcare.domain.repository.AppointmentRepository
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val api: PawCareApiService,
    private val appointmentDao: AppointmentDao
) : AppointmentRepository, SafeApiCall {

    override fun getAppointments(date: String?, status: String?): Flow<Resource<List<Appointment>>> = networkBoundResource(
        query = { 
            if (date != null) {
                appointmentDao.getAppointmentsByDate(date).map { entities -> entities.map { it.toAppointment() } }
            } else {
                appointmentDao.getAllAppointments().map { entities -> entities.map { it.toAppointment() } }
            }
        },
        fetch = { api.getAppointments(date, status).body() ?: emptyList() },
        saveFetchResult = { dtos ->
            appointmentDao.upsertAppointments(dtos.map { it.toEntity() })
        }
    )

    override suspend fun createAppointment(
        date: String,
        timeSlot: String,
        petId: String,
        serviceIds: List<String>,
        notes: String?
    ): Resource<Appointment> {
        val result = safeApiCall {
            api.createAppointment(CreateAppointmentRequest(date, timeSlot, petId, serviceIds, notes))
        }
        return when (result) {
            is Resource.Success -> {
                appointmentDao.upsertAppointments(listOf(result.data.toEntity()))
                Resource.Success(result.data.toAppointment())
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }

    override suspend fun updateAppointmentStatus(
        id: String,
        status: String,
        paymentMethod: String?
    ): Resource<Appointment> {
        val result = safeApiCall {
            api.updateAppointmentStatus(id, UpdateAppointmentStatusRequest(status, paymentMethod))
        }
        return when (result) {
            is Resource.Success -> {
                appointmentDao.upsertAppointments(listOf(result.data.toEntity()))
                Resource.Success(result.data.toAppointment())
            }
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Loading -> Resource.Loading()
        }
    }
}
