package com.example.pawcare.presentation.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object PetRegister : Screen("pet_register")
    
    object ScheduleAppointment : Screen("schedule_appointment?appointmentId={appointmentId}") {
        fun createRoute(appointmentId: String? = null) = 
            if (appointmentId != null) "schedule_appointment?appointmentId=$appointmentId" 
            else "schedule_appointment"
    }
    
    object Payment : Screen("payment/{appointmentId}/{petName}/{service}/{date}/{amount}/{employee}") {
        fun createRoute(appointmentId: String, petName: String, service: String, date: String, amount: Double, employee: String): String {
            val encPet = URLEncoder.encode(petName, StandardCharsets.UTF_8.toString())
            val encService = URLEncoder.encode(service, StandardCharsets.UTF_8.toString())
            val encDate = URLEncoder.encode(date, StandardCharsets.UTF_8.toString())
            val encEmployee = URLEncoder.encode(employee, StandardCharsets.UTF_8.toString())
            return "payment/$appointmentId/$encPet/$encService/$encDate/$amount/$encEmployee"
        }
    }

    object PaymentConfirmation : Screen("payment_confirmation/{receiptNumber}/{amount}/{petName}/{service}/{date}/{method}/{employee}") {
        fun createRoute(receiptNumber: String, amount: Double, petName: String, service: String, date: String, method: String, employee: String): String {
            val encReceipt = URLEncoder.encode(receiptNumber, StandardCharsets.UTF_8.toString())
            val encPet = URLEncoder.encode(petName, StandardCharsets.UTF_8.toString())
            val encService = URLEncoder.encode(service, StandardCharsets.UTF_8.toString())
            val encDate = URLEncoder.encode(date, StandardCharsets.UTF_8.toString())
            val encMethod = URLEncoder.encode(method, StandardCharsets.UTF_8.toString())
            val encEmployee = URLEncoder.encode(employee, StandardCharsets.UTF_8.toString())
            return "payment_confirmation/$encReceipt/$amount/$encPet/$encService/$encDate/$encMethod/$encEmployee"
        }
    }

    object PaymentDetail : Screen("payment_detail/{paymentId}") {
        fun createRoute(paymentId: String) = "payment_detail/$paymentId"
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
    object PaymentList : Screen("payment_list")
}
