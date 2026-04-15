package com.example.pawcare.data.remote

import com.example.pawcare.domain.util.Resource
import retrofit2.Response
import java.io.IOException

interface SafeApiCall {    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                Resource.Success(body)
            } else {
                @Suppress("UNCHECKED_CAST")
                Resource.Success(Unit as T)
            }
        } else {
            val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
            Resource.Error(errorMsg)
        }
    } catch (e: IOException) {
        Resource.Error("No hay conexión a internet.")
    } catch (e: Exception) {
        Resource.Error("Error inesperado: ${e.localizedMessage}")
    }
}
}
