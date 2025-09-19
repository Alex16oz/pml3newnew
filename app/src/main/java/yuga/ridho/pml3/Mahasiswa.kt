package yuga.ridho.pml3

import com.google.firebase.database.IgnoreExtraProperties

// Anotasi ini penting untuk keamanan dan fleksibilitas
@IgnoreExtraProperties
data class Mahasiswa(
    // Memberikan nilai default akan secara otomatis membuat constructor kosong
    val nim: String? = null,
    val namaMhs: String? = null,
    val alamatMhs: String? = null
)