package com.example.pawcare.data.repository

import com.example.pawcare.data.local.dao.PaymentDao
import com.example.pawcare.data.local.entity.PaymentEntity
import com.example.pawcare.domain.model.Payment
import com.example.pawcare.domain.repository.PaymentRepository
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val paymentDao: PaymentDao
) : PaymentRepository {

    override fun getPayments(): Flow<Resource<List<Payment>>> = 
        paymentDao.getAllPayments().map { entities ->
            Resource.Success(entities.map { entity ->
                Payment(
                    id = entity.id,
                    appointmentId = entity.appointmentId,
                    petName = entity.petName,
                    serviceName = entity.serviceName,
                    amount = entity.amount,
                    paymentMethod = entity.paymentMethod,
                    appointmentDate = entity.appointmentDate,
                    paymentDate = entity.paymentDate,
                    paymentTime = entity.paymentTime,
                    employeeName = entity.employeeName,
                    receiptNumber = entity.receiptNumber,
                    status = entity.status
                )
            })
        }

    override suspend fun savePayment(payment: Payment): Resource<Unit> {
        paymentDao.insertPayment(
            PaymentEntity(
                id = payment.id,
                appointmentId = payment.appointmentId,
                petName = payment.petName,
                serviceName = payment.serviceName,
                amount = payment.amount,
                paymentMethod = payment.paymentMethod,
                appointmentDate = payment.appointmentDate,
                paymentDate = payment.paymentDate,
                paymentTime = payment.paymentTime,
                employeeName = payment.employeeName,
                receiptNumber = payment.receiptNumber,
                status = payment.status
            )
        )
        return Resource.Success(Unit)
    }

    override suspend fun getNextReceiptNumber(): String {
        val count = paymentDao.getPaymentsCount() + 1
        return "#${count.toString().padStart(5, '0')}"
    }

    override suspend fun deletePayment(id: String): Resource<Unit> {
        paymentDao.deletePayment(id)
        return Resource.Success(Unit)
    }
}
