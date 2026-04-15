package com.example.pawcare.domain.model

data class Payment(
    val id: String,
    val appointmentId: String,
    val petName: String,
    val serviceName: String,
    val amount: Double,
    val paymentMethod: String,
    val appointmentDate: String, // Fecha y hora de la cita
    val paymentDate: String,     // Fecha del cobro
    val paymentTime: String,     // Hora del cobro
    val employeeName: String,
    val receiptNumber: String,
    val status: String = "Pagado"
)
