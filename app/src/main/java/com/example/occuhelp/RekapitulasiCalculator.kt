// RekapitulasiCalculator.kt
package com.example.occuhelp

import co.yml.charts.common.model.Point
import java.util.Locale

/**
 * Model generik untuk setiap baris data yang akan ditampilkan di tabel rekapitulasi.
 * Ini memungkinkan fleksibilitas untuk jumlah kolom yang berbeda per kategori.
 */
data class RekapitulasiDisplayRow(
    val no: Int,
    val values: List<String>, // Daftar nilai untuk setiap kolom di baris tersebut (setelah "No.")
    val jumlah: Int
)

/**
 * Model untuk data yang akan ditampilkan di UI, termasuk header tabel.
 */
data class RekapitulasiProcessedData(
    val tableHeaders: List<String>,
    val tableRows: List<RekapitulasiDisplayRow>,
    val barChartData: List<BarChartRecapModel>
)

object RekapitulasiCalculator {

    // Helper untuk mengambil nilai numerik dengan aman
    private fun getNumericValue(value: String?): Double {
        if (value.isNullOrBlank()) return Double.NaN
        // Hapus teks tambahan jika ada (misal " Borderline" dari kolesterol)
        val numericPart = value.split(" ")[0].replace(",", ".") // Ganti koma dengan titik untuk desimal
        return numericPart.toDoubleOrNull() ?: Double.NaN
    }

    // Helper untuk parsing Tekanan Darah "Sistolik/Diastolik"
    private fun parseTD(tdString: String?): Pair<Double, Double> {
        if (tdString.isNullOrBlank()) return Pair(Double.NaN, Double.NaN)
        val parts = tdString.split("/")
        if (parts.size == 2) {
            val sistolik = parts[0].trim().toDoubleOrNull() ?: Double.NaN
            val diastolik = parts[1].trim().toDoubleOrNull() ?: Double.NaN
            return Pair(sistolik, diastolik)
        }
        return Pair(Double.NaN, Double.NaN)
    }

    // 1. Rekapitulasi - Gangguan Metabolisme Glukosa
    fun calculateGlukosaRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var peningkatan = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val glukosaPuasa = getNumericValue(item.glukosaPuasa)

            if (glukosaPuasa.isNaN()) {
                tidakAdaData++
            } else if (glukosaPuasa >= 70 && glukosaPuasa <= 105) {
                normal++
            } else if (glukosaPuasa > 105) {
                peningkatan++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal"), normal))
        if (peningkatan > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Peningkatan Glukosa Puasa"), peningkatan))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))

        val headers = listOf("No", "Kategori", "Jumlah")
        return RekapitulasiProcessedData(
            tableHeaders = headers,
            tableRows = rows,
            barChartData = transformToBarChart(rows, headers)
        )
    }

    // 2. Rekapitulasi - Gangguan Status Gizi
    fun calculateStatusGiziRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var underweight = 0
        var normal = 0
        var overweight = 0
        var obeseI = 0
        var obeseII = 0
        var obeseIII = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val imt = getNumericValue(item.imt)
            if (imt.isNaN()) {
                tidakAdaData++
            } else if (imt < 18.4) underweight++
            else if (imt in 18.5..24.9) normal++
            else if (imt in 25.0..29.9) overweight++
            else if (imt in 30.0..34.9) obeseI++
            else if (imt in 35.0..39.9) obeseII++
            else if (imt >= 40) obeseIII++
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (underweight > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Underweight (BMI < 18.4)"), underweight))
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal (BMI 18.5 - 24.9)"), normal))
        if (overweight > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Overweight (BMI 25 - 29.9)"), overweight))
        if (obeseI > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Obesitas Kelas I (BMI 30 - 34.9)"), obeseI))
        if (obeseII > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Obesitas Kelas II (BMI 35 - 39.9)"), obeseII))
        if (obeseIII > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Obesitas Kelas III (BMI >= 40)"), obeseIII))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data IMT"), tidakAdaData))

        val headers = listOf("No", "Gangguan Status Gizi", "Jumlah")
        return RekapitulasiProcessedData(
            tableHeaders = headers,
            tableRows = rows,
            barChartData = transformToBarChart(rows, headers)
        )
    }

    // 3. Rekapitulasi - Gangguan Tekanan Darah
    fun calculateTekananDarahRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        val counts = mutableMapOf(
            "Optimal" to 0, "Normal" to 0, "Prehipertensi" to 0,
            "Hipertensi Grade I" to 0, "Hipertensi Grade II" to 0, "Hipertensi Grade III" to 0,
            "Hipertensi Sistolik Terisolasi" to 0, "Tidak Ada Data" to 0
        )

        data.forEach { item ->
            val (sistolik, diastolik) = parseTD(item.tekananDarah)

            if (sistolik.isNaN() || diastolik.isNaN()) {
                counts["Tidak Ada Data"] = counts["Tidak Ada Data"]!! + 1
            } else if (sistolik >= 180 || diastolik >= 110) {
                counts["Hipertensi Grade III"] = counts["Hipertensi Grade III"]!! + 1
            } else if (sistolik >= 160 || diastolik >= 100) { // Mencakup 160-179 dan 100-109
                counts["Hipertensi Grade II"] = counts["Hipertensi Grade II"]!! + 1
            } else if (sistolik >= 140 || diastolik >= 90) { // Mencakup 140-159 dan 90-99
                // Cek dulu untuk Sistolik Terisolasi
                if (sistolik >= 140 && diastolik < 90) {
                    counts["Hipertensi Sistolik Terisolasi"] = counts["Hipertensi Sistolik Terisolasi"]!! + 1
                } else {
                    counts["Hipertensi Grade I"] = counts["Hipertensi Grade I"]!! + 1
                }
            } else if (sistolik >= 130 || diastolik >= 85) { // Mencakup 130-139 dan 85-89
                counts["Prehipertensi"] = counts["Prehipertensi"]!! + 1
            } else if (sistolik >= 120 || diastolik >= 80) { // Mencakup 120-129 dan 80-84 (Normal menurut beberapa guide)
                counts["Normal"] = counts["Normal"]!! + 1
            } else if (sistolik < 120 && diastolik < 80) {
                counts["Optimal"] = counts["Optimal"]!! + 1
            } else {
                // Kondisi yang mungkin tidak tercover, anggap tidak ada data atau kategori lain
                // Untuk TD, aturan JNC atau ESC/ESH bisa lebih detail.
                // Logika ini mencoba mengikuti urutan dari yang paling parah.
                counts["Tidak Ada Data"] = counts["Tidak Ada Data"]!! + 1 // Fallback
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        counts.filter { it.value > 0 }.forEach { (kategori, jumlah) ->
            rows.add(RekapitulasiDisplayRow(no++, listOf(kategori), jumlah))
        }
        val headers = listOf("No", "Kategori Tekanan Darah", "Jumlah")
        return RekapitulasiProcessedData(
            tableHeaders = headers,
            tableRows = rows,
            barChartData = transformToBarChart(rows, headers)
        )
    }


    // 4. Rekapitulasi - Kelompok Umur Peserta MCU
    fun calculateUmurRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        val counts = mutableMapOf<String, Int>()
        var tidakAdaData = 0
        data.forEach { item ->
            val usia = item.usia
            val jenisKelamin = item.jenisKelamin?.trim()?.lowercase(Locale.ROOT)
            if (usia == null || jenisKelamin.isNullOrBlank()) {
                tidakAdaData++
            } else {
                val kategoriUmur = if (usia < 35) "<35 Tahun" else ">=35 Tahun"
                val key = "${jenisKelamin}_$kategoriUmur"
                counts[key] = (counts[key] ?: 0) + 1
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        // Laki-laki
        if ((counts["laki-laki_<35 Tahun"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Laki-laki", "<35 Tahun"), counts["laki-laki_<35 Tahun"]!!))
        if ((counts["laki-laki_>=35 Tahun"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Laki-laki", ">=35 Tahun"), counts["laki-laki_>=35 Tahun"]!!))
        // Perempuan
        if ((counts["perempuan_<35 Tahun"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Perempuan", "<35 Tahun"), counts["perempuan_<35 Tahun"]!!))
        if ((counts["perempuan_>=35 Tahun"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Perempuan", ">=35 Tahun"), counts["perempuan_>=35 Tahun"]!!))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Diketahui", "Tidak Ada Data"), tidakAdaData))

        val headers = listOf("No", "Jenis Kelamin", "Kategori Umur", "Jumlah")
        return RekapitulasiProcessedData(
            tableHeaders = headers,
            tableRows = rows,
            barChartData = transformToBarChart(rows, headers, combineLabels = true) // combineLabels untuk grafik
        )
    }


    // 5. Rekapitulasi - Kadar Hemoglobin
    fun calculateHbRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        val counts = mutableMapOf<String, Int>()
        var tidakAdaData = 0
        data.forEach { item ->
            val hb = getNumericValue(item.hb)
            val jenisKelamin = item.jenisKelamin?.trim()?.lowercase(Locale.ROOT)

            if (hb.isNaN() || jenisKelamin.isNullOrBlank()) {
                tidakAdaData++
            } else {
                val statusHb = when(jenisKelamin) {
                    "laki-laki" -> if (hb < 13) "Anemia" else "Tidak Anemia"
                    "perempuan" -> if (hb < 12) "Anemia" else "Tidak Anemia"
                    else -> null
                }
                if (statusHb != null) {
                    val key = "${jenisKelamin}_$statusHb"
                    counts[key] = (counts[key] ?: 0) + 1
                } else {
                    tidakAdaData++
                }
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if ((counts["laki-laki_Anemia"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Laki-laki", "Anemia"), counts["laki-laki_Anemia"]!!))
        if ((counts["laki-laki_Tidak Anemia"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Laki-laki", "Tidak Anemia"), counts["laki-laki_Tidak Anemia"]!!))
        if ((counts["perempuan_Anemia"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Perempuan", "Anemia"), counts["perempuan_Anemia"]!!))
        if ((counts["perempuan_Tidak Anemia"] ?: 0) > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Perempuan", "Tidak Anemia"), counts["perempuan_Tidak Anemia"]!!))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Diketahui", "Tidak Ada Data"), tidakAdaData))

        val headers = listOf("No", "Jenis Kelamin", "Status Hb", "Jumlah")
        return RekapitulasiProcessedData(
            tableHeaders = headers,
            tableRows = rows,
            barChartData = transformToBarChart(rows, headers, combineLabels = true)
        )
    }


    // 6. Rekapitulasi - Pemeriksaan Creatinin Darah
    fun calculateCreatininRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData  {
        var normal = 0
        var tidakNormal = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val creatinin = getNumericValue(item.kreatinin)
            if (creatinin.isNaN()) {
                tidakAdaData++
            } else if (creatinin >= 0.6 && creatinin < 1.2) { // Nilai rujukan normal 0.6-1.2 mg/dL. >=1.2 tidak normal.
                // Asumsi < 1.2 adalah normal. Jika 1.2 termasuk normal, ubah jadi <= 1.2
                normal++
            } else { // < 0.6 atau >= 1.2
                tidakNormal++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal (0.6 - <1.2 mg/dL)"), normal))
        if (tidakNormal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Normal"), tidakNormal))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Kategori Kreatinin", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 7. Rekapitulasi - Suspek Gangguan Fungsi Hati
    fun calculateFungsiHatiRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var peningkatan = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val sgot = getNumericValue(item.sgot)
            val sgpt = getNumericValue(item.sgpt)

            if (sgot.isNaN() || sgpt.isNaN()) {
                tidakAdaData++
            } else if (sgot > 34 || sgpt > 55) {
                peningkatan++
            } else { // SGOT <= 34 AND SGPT <= 55
                normal++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (peningkatan > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Peningkatan SGOT dan/atau SGPT"), peningkatan))
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal"), normal))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Status Fungsi Hati", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 8. Rekapitulasi - Pemeriksaan Kolestrol Total
    fun calculateKolesterolTotalRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var batasTinggi = 0
        var tinggi = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val kolesterolTotal = getNumericValue(item.kolesterolTotal)

            if (kolesterolTotal.isNaN()) {
                tidakAdaData++
            } else if (kolesterolTotal < 200) {
                normal++
            } else if (kolesterolTotal in 200.0..239.0) {
                batasTinggi++
            } else if (kolesterolTotal >= 240) {
                tinggi++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal (<200 mg/dL)"), normal))
        if (batasTinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Batas Tinggi (200-239 mg/dL)"), batasTinggi))
        if (tinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tinggi (>=240 mg/dL)"), tinggi))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Kategori Kol. Total", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 9. Rekapitulasi - Riwayat Kesehatan Peserta MCU
    fun calculateRiwayatKesehatanRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        val keywords = mapOf(
            "batu empedu" to "Batu Empedu",
            "dm" to "DM (Diabetes Mellitus)",
            "diabetes mellitus" to "DM (Diabetes Mellitus)",
            "dyspepsia" to "Dyspepsia",
            "thypoid" to "Thypoid", // Jika ejaan dari data adalah "thypoid"
            "typhoid" to "Thypoid", // Tambahkan variasi ejaan jika perlu
            "penyakit jantung" to "Penyakit Jantung",
            "tbc" to "TBC",
            "keluhan muskoloskeletal" to "Keluhan Muskoloskeletal",
            "asma" to "Asma",
            "hipertensi" to "Hipertensi",
            "covid-19" to "Covid-19"
        )
        // Inisialisasi counts dengan semua nilai canonical dari keywords
        val counts = keywords.values.distinct().associateWith { 0 }.toMutableMap()
        var tidakAdaDataRiwayat = 0

        data.forEach { item ->
            val riwayatString = item.riwayatKesehatanPribadi
            if (riwayatString.isNullOrBlank()) {
                tidakAdaDataRiwayat++
            } else {
                val lowerRiwayat = riwayatString.lowercase(Locale.ROOT)
                val foundKeywordsForThisPatient = mutableSetOf<String>()

                for ((key, canonicalName) in keywords) {
                    if (Regex("\\b${Regex.escape(key)}\\b", RegexOption.IGNORE_CASE).containsMatchIn(lowerRiwayat)) {
                        // Hanya tambahkan ke counts jika canonicalName belum dihitung untuk pasien ini
                        if (foundKeywordsForThisPatient.add(canonicalName)) {
                            counts[canonicalName] = counts[canonicalName]!! + 1
                        }
                    }
                }
            }
        }

        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        // Tampilkan hanya keyword yang ada di daftar dan jumlahnya > 0
        keywords.values.distinct().forEach { kategori ->
            if (counts[kategori]!! > 0) {
                rows.add(RekapitulasiDisplayRow(no++, listOf(kategori), counts[kategori]!!))
            }
        }
        if (tidakAdaDataRiwayat > 0) {
            rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data Riwayat"), tidakAdaDataRiwayat))
        }
        val headers = listOf("No", "Kategori Penyakit", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 10. Rekapitulasi - Pemeriksaan Kolesterol HDL
    fun calculateKolesterolHdlRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var rendah = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val hdl = getNumericValue(item.kolesHdl)
            if (hdl.isNaN()) {
                tidakAdaData++
            } else if (hdl < 40) {
                rendah++
            } else { // HDL >= 40
                normal++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal (>=40 mg/dL)"), normal))
        if (rendah > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Rendah (<40 mg/dL)"), rendah))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Kategori Kol. HDL", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 11. Rekapitulasi - Pemeriksaan Kolesterol LDL
    fun calculateKolesterolLdlRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var optimal = 0
        var hampirOptimal = 0
        var batasTinggi = 0
        // Kategori "tinggi" (160-189 mg/dL) tidak ada di deskripsi Anda, tapi umumnya ada. Saya tambahkan.
        var tinggi = 0
        var sangatTinggi = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val ldl = getNumericValue(item.kolesLdl)
            if (ldl.isNaN()) {
                tidakAdaData++
            } else if (ldl < 100) {
                optimal++
            } else if (ldl in 100.0..129.0) {
                hampirOptimal++
            } else if (ldl in 130.0..159.0) {
                batasTinggi++
            } else if (ldl in 160.0..189.0) { // Asumsi kategori ini
                tinggi++
            } else if (ldl >= 190) {
                sangatTinggi++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (optimal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Optimal (<100 mg/dL)"), optimal))
        if (hampirOptimal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Hampir Optimal (100-129 mg/dL)"), hampirOptimal))
        if (batasTinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Batas Tinggi (130-159 mg/dL)"), batasTinggi))
        if (tinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tinggi (160-189 mg/dL)"), tinggi))
        if (sangatTinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Sangat Tinggi (>=190 mg/dL)"), sangatTinggi))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Kategori Kol. LDL", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 12. Rekapitulasi - Pemeriksaan Trigliserida
    fun calculateTrigliseridaRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var batasTinggi = 0
        var tinggi = 0
        var sangatTinggi = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val trigliserida = getNumericValue(item.trigliserid)
            if (trigliserida.isNaN()) {
                tidakAdaData++
            } else if (trigliserida < 150) {
                normal++
            } else if (trigliserida in 150.0..199.0) {
                batasTinggi++
            } else if (trigliserida in 200.0..499.0) {
                tinggi++
            } else if (trigliserida >= 500) {
                sangatTinggi++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal (<150 mg/dL)"), normal))
        if (batasTinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Batas Tinggi (150-199 mg/dL)"), batasTinggi))
        if (tinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tinggi (200-499 mg/dL)"), tinggi))
        if (sangatTinggi > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Sangat Tinggi (>=500 mg/dL)"), sangatTinggi))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Kategori Trigliserida", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 13. Rekapitulasi - Pemeriksaan Ureum
    fun calculateUreumRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var tidakNormal = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val ureum = getNumericValue(item.ureum)
            if (ureum.isNaN()) {
                tidakAdaData++
            } else if (ureum >= 21 && ureum <= 43) { // Nilai rujukan normal 21-43 mg/dL. >=43 tidak normal (Anda menulis =43, saya asumsikan >43 atau >=43 adalah tidak normal)
                // Jika 43 termasuk normal, maka <= 43. Jika 43 sudah tidak normal, maka < 43.
                // Saya ikuti deskripsi Anda: 21-43 normal, lebih dari =43 tidak normal. Berarti <=43 normal.
                normal++
            } else { // < 21 atau > 43
                tidakNormal++
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal (21-43 mg/dL)"), normal))
        if (tidakNormal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Normal (<21 atau >43 mg/dL)"), tidakNormal))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Kategori Ureum", "Jumlah")
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // 14. Rekapitulasi - Asam Urat
    fun calculateAsamUratRekap(data: List<McuRawDataItem>): RekapitulasiProcessedData {
        var normal = 0
        var tidakNormal = 0
        var tidakAdaData = 0

        data.forEach { item ->
            val asamUrat = getNumericValue(item.asamUrat)
            val jenisKelamin = item.jenisKelamin?.trim()?.lowercase(Locale.ROOT)

            if (asamUrat.isNaN() || jenisKelamin.isNullOrBlank()) {
                tidakAdaData++
            } else {
                var isNormalBasedOnGender = false
                if (jenisKelamin == "laki-laki") {
                    if (asamUrat >= 3.5 && asamUrat < 7.2) { // Laki-laki: Normal 3.5 - 7.2 mg/dL. >= 7.2 Tidak Normal
                        // Asumsi < 7.2 adalah normal
                        isNormalBasedOnGender = true
                    }
                } else if (jenisKelamin == "perempuan") {
                    if (asamUrat >= 2.6 && asamUrat < 6.0) { // Perempuan: Normal 2.6 - 6.0 mg/dL. >= 6.0 Tidak Normal
                        // Asumsi < 6.0 adalah normal
                        isNormalBasedOnGender = true
                    }
                } else {
                    // Jenis kelamin tidak dikenali, anggap tidak ada data untuk klasifikasi ini
                    tidakAdaData++
                    return@forEach // Lanjut ke item berikutnya
                }

                if (isNormalBasedOnGender) {
                    normal++
                } else {
                    tidakNormal++
                }
            }
        }
        val rows = mutableListOf<RekapitulasiDisplayRow>()
        var no = 1
        if (normal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Normal"), normal))
        if (tidakNormal > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Normal"), tidakNormal))
        if (tidakAdaData > 0) rows.add(RekapitulasiDisplayRow(no++, listOf("Tidak Ada Data"), tidakAdaData))
        val headers = listOf("No", "Status Asam Urat", "Jumlah") // Atau tambahkan "Jenis Kelamin" jika perlu
        return RekapitulasiProcessedData(headers, rows, transformToBarChart(rows, headers))
    }

    // Modifikasi transformToBarChart untuk menangani RekapitulasiDisplayRow
    // dan opsi untuk menggabungkan label jika ada beberapa kolom kategori
    private fun transformToBarChart(
        tableRows: List<RekapitulasiDisplayRow>,
        headers: List<String>, // Ambil header untuk referensi label
        combineLabels: Boolean = false, // True jika label grafik harus gabungan dari beberapa kolom kategori
        defaultDescPrefix: String = ""
    ): List<BarChartRecapModel> {
        return tableRows
            .filterNot { row -> row.values.any { it.contains("Tidak Ada Data", ignoreCase = true) } }
            .mapIndexed { index, row ->
                val chartLabel = if (combineLabels) {
                    // Gabungkan values[0] (misal Jenis Kelamin) dan values[1] (misal Kategori Umur)
                    // Hati-hati dengan panjang label.
                    // Ambil 2-3 kata pertama dari masing-masing atau singkatan.
                    val labelPart1 = row.values.getOrNull(0)?.split(" ")?.take(1)?.joinToString(" ") ?: ""
                    val labelPart2 = row.values.getOrNull(1)?.split(" ")?.take(1)?.joinToString(" ") ?: ""
                    "$labelPart1 $labelPart2".trim().take(10) // Batasi panjang
                } else {
                    // Gunakan nilai dari kolom kategori utama (biasanya values[0])
                    row.values.firstOrNull()?.take(7)?.uppercase(Locale.ROOT) ?: "N/A"
                }

                BarChartRecapModel(
                    label = chartLabel,
                    point = Point((index + 1).toFloat(), row.jumlah.toFloat()),
                    description = "$defaultDescPrefix${row.jumlah}"
                )
            }
    }

    // Implementasikan fungsi kalkulator lainnya (Creatinin, Fungsi Hati, Kolesterol Total, Riwayat, HDL, LDL, Trigliserida, Ureum, Asam Urat)
    // dengan pola yang sama:
    // - Ambil nilai relevan dari McuRawDataItem menggunakan getNumericValue() atau parsing khusus.
    // - Lakukan klasifikasi berdasarkan parameter yang diberikan.
    // - Kembalikan List<RekapitulasiDisplayRow>.
}