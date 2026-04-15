package com.example.pawcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val appointmentId: String,
    val petName: String,
    val serviceName: String,
    val amount: Double,
    val paymentMethod: String,
    val appointmentDate: String,
    val paymentDate: String,
    val paymentTime: String,
    val employeeName: String,
    val receiptNumber: String,
    val status: String
)
