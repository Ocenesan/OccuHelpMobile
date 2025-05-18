package com.example.occuhelp

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class LoginRequest(
    val nip: String,
    val password: String
)

data class AuthResponse(
    val token: String, // Asumsi API mengembalikan token
    val user: User,
    val message: String? = null // Pesan sukses opsional
)

// Digunakan untuk response dasar dari endpoint seperti change-password, send-reset-link
data class BaseResponse(
    val message: String?,
    val error: String? = null, // Pesan error opsional
    // Tambahkan field lain jika API mengembalikan status atau data tambahan
)

// Digunakan untuk request body ke endpoint /change-password
data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String
)

// Digunakan untuk request body ke endpoint /send-reset-link
data class ResetLinkRequest(
    val email: String
)

// Digunakan untuk request body ke endpoint /reset-password
data class ResetPasswordRequest(
    val token: String,
    val email: String,
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

// Digunakan untuk request body ke endpoint /users/{id} (PATCH)
data class UpdateUserRequest(
    val name: String?, // Buat nullable jika tidak semua field wajib diupdate
    val email: String?
    // Tambahkan field lain yang bisa diupdate, role mungkin tidak bisa diupdate user biasa
)

data class User(
    val id: Int,
    val name: String,
    val nip: String,
    @SerializedName("nip_verified_at")
    val nipVerifiedAt: Timestamp?, // bisa gunakan LocalDateTime jika parsing waktu
    val email: String,
    val password: String,
    val role: String
)

data class PasswordToken(
    val nip: String,
    val token: String,
    @SerializedName("created_at")
    val createdAt: Timestamp?
)

data class Patient(
    val id: Int,
    @SerializedName("med_record_id")
    val medRecordId: String,
    @SerializedName("patient_id")
    val patientId: String,
    val name: String,
    val unit: String?,
    val gender: String,
    val age: Int,
    @SerializedName("birth_date")
    val birthDate: String, // Format: "YYYY-MM-DD"
    val jabatan: String?,
    val ketenagaan: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class McuPatient(
    val id: Int,
    @SerializedName("patient_id")
    val patientId: Int,
    val name: String,
    @SerializedName("examination_date")
    val examDate: String, // Format ISO: "YYYY-MM-DD"
    @SerializedName("examination_type")
    val examType: String,
    val status: McuStatus,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

enum class McuStatus {
    Completed,
    Process,
    Canceled
}

data class McuResult(
    val id: Int,
    @SerializedName("patient_id")
    val patientId: Int,
    val category: String,
    val result: String,
    @SerializedName("result_date")
    val resultDate: String, // Format: "YYYY-MM-DD"
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)