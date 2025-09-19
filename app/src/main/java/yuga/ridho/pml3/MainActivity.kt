package yuga.ridho.pml3

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import yuga.ridho.pml3.ui.theme.Pml3newnewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pml3newnewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Memanggil Composable utama aplikasi
                    MahasiswaScreen()
                }
            }
        }
    }
}

@Composable
fun MahasiswaScreen() {
    // Konteks aplikasi untuk menampilkan Toast
    val context = LocalContext.current
    // Referensi ke tabel "TabelMahasiswa" di Firebase Database
    val database = FirebaseDatabase.getInstance().getReference("TabelMahasiswa")

    // State untuk menampung input dari pengguna
    var nim by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }

    // State untuk menampung daftar mahasiswa dari Firebase
    var mahasiswaList by remember { mutableStateOf<List<Mahasiswa>>(emptyList()) }

    // LaunchedEffect digunakan untuk mengambil data dari Firebase sekali saat Composable pertama kali dibuat
    LaunchedEffect(Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Mahasiswa>()
                for (dataSnapshot in snapshot.children) {
                    val mahasiswa = dataSnapshot.getValue(Mahasiswa::class.java)
                    mahasiswa?.let { list.add(it) }
                }
                mahasiswaList = list
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        // Menambahkan listener ke referensi database
        database.addValueEventListener(valueEventListener)
    }

    // Tampilan utama menggunakan Column
    Column(modifier = Modifier.padding(16.dp)) {
        // Input field untuk NIM
        OutlinedTextField(
            value = nim,
            onValueChange = { nim = it },
            label = { Text("NIM") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Input field untuk Nama Mahasiswa
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama Mahasiswa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Input field untuk Alamat
        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text("Alamat") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Baris untuk tombol-tombol aksi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Tombol Insert
            Button(onClick = {
                if (nim.isNotBlank() && nama.isNotBlank()) {
                    val mahasiswa = Mahasiswa(nim, nama, alamat)
                    database.child(nim).setValue(mahasiswa)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            // Mengosongkan input field setelah berhasil
                            nim = ""
                            nama = ""
                            alamat = ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Gagal menambahkan data", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "NIM dan Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Insert")
            }
            // Tombol Delete
            Button(onClick = {
                if (nim.isNotBlank()) {
                    database.child(nim).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            // Mengosongkan input field setelah berhasil
                            nim = ""
                            nama = ""
                            alamat = ""
                        }
                }
            }) {
                Text("Delete")
            }
            // Tombol Update
            Button(onClick = {
                if (nim.isNotBlank()) {
                    val mahasiswaData = mapOf(
                        "namaMhs" to nama,
                        "alamatMhs" to alamat
                    )
                    database.child(nim).updateChildren(mahasiswaData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        }
                }
            }) {
                Text("Update")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Daftar mahasiswa yang dapat di-scroll
        LazyColumn {
            items(mahasiswaList) { mahasiswa ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            // Saat item di-klik, isi input field dengan data mahasiswa tersebut
                            nim = mahasiswa.nim
                            nama = mahasiswa.namaMhs
                            alamat = mahasiswa.alamatMhs
                        },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "NIM: ${mahasiswa.nim}")
                        Text(text = "Nama: ${mahasiswa.namaMhs}")
                        Text(text = "Alamat: ${mahasiswa.alamatMhs}")
                    }
                }
            }
        }
    }
}