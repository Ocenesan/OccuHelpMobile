package com.example.occuhelp

import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    // ========== Auth Routes ==========
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("user")
    suspend fun getUserProfile(): Response<User> // requires token (auth:sanctum)

    @POST("logout")
    suspend fun logout(): Response<Void> // token required

    @GET("me")
    suspend fun getMe(): Response<User> // token required

    @PATCH("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UpdateUserRequest): Response<User>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Void>

    @POST("change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<BaseResponse>

    @POST("send-reset-link")
    suspend fun sendResetLink(@Body request: ResetLinkRequest): Response<BaseResponse>

    @POST("reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<BaseResponse>

    // ========== Patients ==========
    @GET("patients")
    suspend fun getPatients(): Response<PatientsResponse>

    @GET("patients/{id}")
    suspend fun getPatientById(@Path("id") id: Int): Response<Patient>

    // ========== MCU Patients ==========
    @GET("mcu-patients")
    suspend fun getMcuPatients(): Response<List<McuPatient>>

    @GET("mcu-patients/{id}")
    suspend fun getMcuPatient(@Path("id") id: Int): Response<McuPatient>

    // ========== MCU Results ==========
    @GET("mcu-results")
    suspend fun getMcuResults(): Response<List<McuResult>>

    @GET("mcu-results/by-patient/{id}")
    suspend fun getMcuResultsByPatient(@Path("id") patientId: Int): Response<List<McuResult>>

    @GET("mcu-results/{id}")
    suspend fun getMcuResult(@Path("id") id: Int): Response<McuResult>
}