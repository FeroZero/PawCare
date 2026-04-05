package com.example.pawcare.domain.repository

import com.example.pawcare.domain.model.Service
import com.example.pawcare.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun getServices(): Flow<Resource<List<Service>>>
}
