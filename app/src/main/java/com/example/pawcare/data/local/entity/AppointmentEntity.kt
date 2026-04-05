package com.example.pawcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val date: String,
    val timeSlot: String,
    val status: String,
    val totalPrice: Double,
    val paymentMethod: String?,
    val notes: String?,
    val petId: String,
    val petName: String,
    val petPhotoUrl: String?,
    val serviceIds: String
)
