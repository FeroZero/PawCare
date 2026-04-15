package com.example.pawcare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pawcare.data.local.entity.AppointmentEntity
import com.example.pawcare.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE date = :date")
    fun getAppointmentsByDate(date: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE petId = :petId")
    fun getAppointmentsByPetId(petId: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM services WHERE id IN (:ids)")
    suspend fun getServicesByIds(ids: List<String>): List<ServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: String)
}
