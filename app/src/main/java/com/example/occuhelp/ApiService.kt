package com.example.occuhelp

import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    // ========== Auth Routes ==========
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("/api/user")
    suspend fun getUserProfile(): Response<User> // requires token (auth:sanctum)

    @POST("/api/logout")
    suspend fun logout(): Response<Void> // token required

    @GET("/api/me")
    suspend fun getMe(): Response<User> // token required

    @PATCH("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UpdateUserRequest): Response<User>

    @DELETE("/api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Void>

    @POST("/api/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<BaseResponse>

    @POST("/api/send-reset-link")
    suspend fun sendResetLink(@Body request: ResetLinkRequest): Response<BaseResponse>

    @POST("/api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<BaseResponse>

    // ========== Patients ==========
    @GET("/api/patients")
    suspend fun getPatients(): Response<List<Patient>>

    @GET("/api/patients/{id}")
    suspend fun getPatientById(@Path("id") id: Int): Response<Patient>

    @DELETE("/api/patients/{id}")
    suspend fun deletePatient(@Path("id") id: Int): Response<Void>

    // ========== MCU Patients ==========
    @GET("/api/mcu-patients")
    suspend fun getMcuPatients(): Response<List<McuPatient>>

    @GET("/api/mcu-patients/{id}")
    suspend fun getMcuPatient(@Path("id") id: Int): Response<McuPatient>

    @DELETE("/api/mcu-patients/{id}")
    suspend fun deleteMcuPatient(@Path("id") id: Int): Response<Void>

    // ========== MCU Results ==========
    @GET("/api/mcu-results")
    suspend fun getMcuResults(): Response<List<McuResult>>

    @GET("/api/mcu-results/by-patient/{id}")
    suspend fun getMcuResultsByPatient(@Path("id") patientId: Int): Response<List<McuResult>>

    @GET("/api/mcu-results/{id}")
    suspend fun getMcuResult(@Path("id") id: Int): Response<McuResult>

    @DELETE("/api/mcu-results/{id}")
    suspend fun deleteMcuResult(@Path("id") id: Int): Response<Void>
}