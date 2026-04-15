package com.example.pawcare.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object PetRegister : Screen("pet_register")
    
    // Ruta de agendado con parámetro opcional para edición
    object ScheduleAppointment : Screen("schedule_appointment?appointmentId={appointmentId}") {
        fun createRoute(appointmentId: String? = null) = 
            if (appointmentId != null) "schedule_appointment?appointmentId=$appointmentId" 
            else "schedule_appointment"
    }
    
    object AppointmentConfirmation : Screen("appointment_confirmation/{date}/{time}/{petName}/{serviceName}") {
        fun createRoute(date: String, time: String, petName: String, serviceName: String): String {
            val encPet = URLEncoder.encode(petName, StandardCharsets.UTF_8.toString())
            val encService = URLEncoder.encode(serviceName, StandardCharsets.UTF_8.toString())
            return "appointment_confirmation/$date/$time/$encPet/$encService"
        }
    }

    object PetConfirmation : Screen("pet_confirmation/{petId}") {
        fun createRoute(petId: String) = "pet_confirmation/$petId"
    }

    object PetList : Screen("pet_list")
    object PetProfile : Screen("pet_profile/{petId}") {
        fun createRoute(petId: String) = "pet_profile/$petId"
    }
    object ProductList : Screen("product_list")
    object AppointmentList : Screen("appointment_list")
}
