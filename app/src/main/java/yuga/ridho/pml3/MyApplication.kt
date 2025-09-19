package yuga.ridho.pml3

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Baris ini secara manual menginisialisasi Firebase saat aplikasi dimulai
        FirebaseApp.initializeApp(this)
    }
}