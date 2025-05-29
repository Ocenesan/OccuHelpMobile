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

data class PatientsResponse(
    val patients: List<Patient>
    // Jika ada field lain di level root objek JSON, tambahkan di sini
    // misalnya: val total_pages: Int?, val current_page: Int?, dll.
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
    val updatedAt: String,
    @SerializedName("examination_date")
    val examDate: String?
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
//    @SerializedName("created_at")
//    val createdAt: String,
//    @SerializedName("updated_at")
//    val updatedAt: String
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
//    @SerializedName("created_at")
//    val createdAt: String,
//    @SerializedName("updated_at")
//    val updatedAt: String
)

data class McuResultsResponse(
    // Ganti nama "results" jika properti di JSON berbeda
    val results: List<McuResult> // Atau nama properti yang sesuai dari JSON API
    // Misalnya jika JSON nya: { "data_mcu": [...] } maka:
    // @SerializedName("data_mcu")
    // val mcuResultsList: List<McuResult>
)

data class McuRawDataItem(
    val id: Int?, // Buat nullable jika ada kemungkinan tidak ada
    @SerializedName("mcu_patient_id")
    val mcuPatientId: Int?,
    val name: String?,
    @SerializedName("Jenis Kelamin")
    val jenisKelamin: String?,
    @SerializedName("Usia")
    val usia: Int?, // Atau Double? jika bisa desimal, lalu konversi
    @SerializedName("examination_date")
    val examinationDate: String?,
    val unit: String?,
    val jabatan: String?,
    val ketenagaan: String?,
    val saran: String?,
    @SerializedName("BB (Kg)")
    val bbKg: String?, // String karena bisa jadi ada teks, parse ke Double nanti
    @SerializedName("TB (Cm)")
    val tbCm: String?, // String, parse ke Double
    @SerializedName("IMT")
    val imt: String?, // String, parse ke Double
    @SerializedName("Kategori IMT")
    val kategoriImt: String?,
    @SerializedName("Tekanan Darah")
    val tekananDarah: String?, // Format "Sistolik/Diastolik"
    @SerializedName("Kategori Tekanan Darah")
    val kategoriTekananDarah: String?,
    @SerializedName("Riwayat Kesehatan Pribadi")
    val riwayatKesehatanPribadi: String?,
    @SerializedName("Riwayat Kesehatan Keluarga")
    val riwayatKesehatanKeluarga: String?,
    @SerializedName("Anamnesa")
    val anamnesa: String?,
    @SerializedName("Merokok")
    val merokok: String?,
    @SerializedName("Alkohol")
    val alkohol: String?,
    @SerializedName("EKG")
    val ekg: String?,
    @SerializedName("Kreatinin")
    val kreatinin: String?, // String, parse ke Double
    @SerializedName("Egfr")
    val egfr: String?, // String, parse ke Double
    @SerializedName("Ureum")
    val ureum: String?, // String, parse ke Double
    @SerializedName("Glukosa Puasa")
    val glukosaPuasa: String?, // String, parse ke Double
    @SerializedName("Asam Urat")
    val asamUrat: String?, // String, parse ke Double
    @SerializedName("Basofil")
    val basofil: String?,
    @SerializedName("Eosinofil")
    val eosinofil: String?,
    @SerializedName("Hb")
    val hb: String?, // String, parse ke Double
    @SerializedName("Hematokrit")
    val hematokrit: String?,
    @SerializedName("Trombosit")
    val trombosit: String?,
    @SerializedName("Eritrosit (Urine)") // Nama field unik jika ada Eritrosit darah
    val eritrositUrine: String?,
    @SerializedName("Lekosit")
    val lekosit: String?,
    @SerializedName("Mch")
    val mch: String?,
    @SerializedName("Mchc")
    val mchc: String?,
    @SerializedName("Mcv")
    val mcv: String?,
    @SerializedName("Limfosit")
    val limfosit: String?,
    @SerializedName("Monosit")
    val monosit: String?,
    @SerializedName("Neutrofil")
    val neutrofil: String?,
    @SerializedName("Neutrofil Limfosit Ratio")
    val neutrofilLimfositRatio: String?,
    @SerializedName("Rdw-Cv")
    val rdwCv: String?,
    @SerializedName("Koles Hdl")
    val kolesHdl: String?, // String, parse ke Double
    @SerializedName("Koles Ldl")
    val kolesLdl: String?, // String, parse ke Double
    @SerializedName("Trigliserid")
    val trigliserid: String?, // String, parse ke Double
    @SerializedName("Sgot")
    val sgot: String?, // String, parse ke Double
    @SerializedName("Sgpt")
    val sgpt: String?, // String, parse ke Double
    @SerializedName("Kolesterol") // Ini Kolesterol Total
    val kolesterolTotal: String?, // String, parse ke Double, mungkin ada teks "Borderline"
    @SerializedName("pH")
    val phUrine: String?,
    @SerializedName("Warna")
    val warnaUrine: String?,
    @SerializedName("Kejernihan")
    val kejernihanUrine: String?,
    @SerializedName("Lekosit (Urine)")
    val lekositUrine: String?,
    @SerializedName("Epitel")
    val epitelUrine: String?,
    @SerializedName("Bakteri")
    val bakteriUrine: String?,
    @SerializedName("Silinder")
    val silinderUrine: String?,
    @SerializedName("Kristal")
    val kristalUrine: String?,
    @SerializedName("Berat Jenis")
    val beratJenisUrine: String?,
    @SerializedName("Protein")
    val proteinUrine: String?,
    @SerializedName("Glukosa (Urine)")
    val glukosaUrine: String?,
    @SerializedName("Keton")
    val ketonUrine: String?,
    @SerializedName("Darah (Urine)")
    val darahUrine: String?,
    @SerializedName("Bilirubin")
    val bilirubinUrine: String?,
    @SerializedName("Urobilinogen")
    val urobilinogenUrine: String?,
    @SerializedName("Nitrit")
    val nitritUrine: String?,
    @SerializedName("Lekosit Esterase")
    val lekositEsteraseUrine: String?,
    @SerializedName("Rasio Albumin/Kreatinin")
    val rasioAlbuminKreatinin: String?,
    @SerializedName("Laboratorium (Summary)")
    val laboratoriumSummary: String?,
    @SerializedName("Radiologi")
    val radiologi: String?,
    @SerializedName("Pemeriksaan Fisik")
    val pemeriksaanFisik: String?,
    @SerializedName("Visus")
    val visus: String?,
    @SerializedName("Buta Warna")
    val butaWarna: String?
    // Tambahkan field lain jika ada
)

// Data class untuk output tabel yang lebih seragam
data class RekapitulasiTableRow(
    val no: Int,
    val kategori1: String, // Kolom utama (misal "Kategori", "Gangguan Status Gizi")
    val kategori2: String? = null, // Kolom kedua jika ada (misal "Jenis Kelamin" untuk Umur atau Hb)
    val kategori3: String? = null, // Kolom ketiga jika ada
    val jumlah: Int
) {
    // Helper untuk menentukan header tabel berdasarkan kategori2 dan kategori3
    fun getTableHeaders(): List<String> {
        val headers = mutableListOf("No.", kategori1)
        kategori2?.let { headers.add(it) }
        kategori3?.let { headers.add(it) }
        headers.add("Jumlah")
        return headers
    }
}