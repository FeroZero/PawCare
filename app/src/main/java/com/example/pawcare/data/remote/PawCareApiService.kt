package com.example.pawcare.data.remote

import com.example.pawcare.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface PawCareApiService {

    @POST("owners")
    suspend fun createOwner(@Body request: CreateOwnerRequest): Response<OwnerDto>

    @GET("owners/{id}")
    suspend fun getOwnerById(@Path("id") id: String): Response<OwnerDto>

    @GET("owners/{id}/pets")
    suspend fun getOwnerPets(@Path("id") id: String): Response<List<PetDto>>

    @POST("pets")
    suspend fun createPet(@Body request: CreatePetRequest): Response<PetDto>

    @GET("pets")
    suspend fun searchPets(@Query("search") query: String?): Response<List<PetDto>>

    @GET("pets/{id}")
    suspend fun getPetById(@Path("id") id: String): Response<PetDto>

    @PUT("pets/{id}")
    suspend fun updatePet(@Path("id") id: String, @Body request: CreatePetRequest): Response<PetDto>

    @GET("pets/{id}/history")
    suspend fun getPetHistory(@Path("id") id: String): Response<List<AppointmentDto>>

    @GET("services")
    suspend fun getServices(): Response<List<ServiceDto>>

    @POST("appointments")
    suspend fun createAppointment(@Body request: CreateAppointmentRequest): Response<AppointmentDto>

    @GET("appointments")
    suspend fun getAppointments(
        @Query("date") date: String?,
        @Query("status") status: String?
    ): Response<List<AppointmentDto>>

    @PATCH("appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: String,
        @Body request: UpdateAppointmentStatusRequest
    ): Response<AppointmentDto>

    @GET("products")
    suspend fun getProducts(@Query("category") category: String?): Response<List<ProductDto>>

    @POST("products")
    suspend fun createProduct(@Body request: CreateProductRequest): Response<ProductDto>
}
