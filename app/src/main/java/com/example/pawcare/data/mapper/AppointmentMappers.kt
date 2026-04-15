package com.example.pawcare.data.mapper

import com.example.pawcare.data.local.entity.AppointmentEntity
import com.example.pawcare.data.remote.dto.AppointmentDto
import com.example.pawcare.domain.model.Appointment

fun AppointmentDto.toAppointment(): Appointment {
    return Appointment(
        id = id,
        date = date,
        timeSlot = timeSlot,
        status = status,
        totalPrice = totalPrice,
        paymentMethod = paymentMethod,
        notes = notes,
        petId = pet.id,
        petName = pet.name,
        petPhotoUrl = pet.photoUrl,
        services = services.map { it.toService() }
    )
}

fun AppointmentDto.toEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = id,
        date = date,
        timeSlot = timeSlot,
        status = status,
        totalPrice = totalPrice,
        paymentMethod = paymentMethod,
        notes = notes,
        petId = pet.id,
        petName = pet.name,
        petPhotoUrl = pet.photoUrl,
        serviceIds = services.joinToString(",") { it.id }
    )
}

fun Appointment.toEntity(): AppointmentEntity {
    return AppointmentEntity(
        id = id,
        date = date,
        timeSlot = timeSlot,
        status = status,
        totalPrice = totalPrice,
        paymentMethod = paymentMethod,
        notes = notes,
        petId = petId,
        petName = petName,
        petPhotoUrl = petPhotoUrl,
        serviceIds = services.joinToString(",") { it.id }
    )
}

fun AppointmentEntity.toAppointment(): Appointment {
    return Appointment(
        id = id,
        date = date,
        timeSlot = timeSlot,
        status = status,
        totalPrice = totalPrice,
        paymentMethod = paymentMethod,
        notes = notes,
        petId = petId,
        petName = petName,
        petPhotoUrl = petPhotoUrl,
        services = emptyList() 
    )
}
