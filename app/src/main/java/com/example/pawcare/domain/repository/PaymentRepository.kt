package com.example.pawcare.domain.repository

import com.example.pawcare.domain.model.Payment
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getPayments(): Flow<Resource<List<Payment>>>
    suspend fun savePayment(payment: Payment): Resource<Unit>
    suspend fun getNextReceiptNumber(): String
    suspend fun deletePayment(id: String): Resource<Unit>
}
