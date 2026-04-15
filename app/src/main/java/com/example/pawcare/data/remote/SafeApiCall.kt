package com.example.pawcare.data.remote

import com.example.pawcare.domain.util.Resource
import retrofit2.Response
import java.io.IOException

interface SafeApiCall {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    @Suppress("UNCHECKED_CAST")
                    Resource.Success(Unit as T)
                } else {
                    Resource.Success(body)
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                val message = if (errorBody.contains("<!DOCTYPE html>") || errorBody.length > 200) {
                    "Error en el servidor (${response.code()})"
                } else if (errorBody.isNotBlank()) {
                    errorBody
                } else {
                    "Error: ${response.code()} ${response.message()}"
                }
                Resource.Error(message)
            }
        } catch (e: IOException) {
            Resource.Error("Revisa tu conexión a internet")
        } catch (e: Exception) {
            Resource.Error("Ha ocurrido un error inesperado")
        }
    }
}
