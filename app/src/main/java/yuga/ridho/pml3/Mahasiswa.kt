package yuga.ridho.pml3

import androidx.compose.runtime.Immutable

@Immutable
data class Mahasiswa(
    val nim: String = "",
    val namaMhs: String = "",
    val alamatMhs: String = ""
)