package com.example.pawcare.data.remote

import com.example.pawcare.domain.util.Resource
import retrofit2.Response

interface SafeApiCall {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return Resource.Success(body)
                }
            }
            return Resource.Error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown Error")
        }
    }
}
