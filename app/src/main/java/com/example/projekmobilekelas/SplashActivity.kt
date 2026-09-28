package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Tampilkan splash screen selama 3 detik
        Handler(Looper.getMainLooper()).postDelayed({
            // Cek status login dari SharedPreferences
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isLoggedIn = sharedPreferences.getBoolean("logged_in", false)

            if (isLoggedIn) {
                // Jika sudah login, buka MainActivity
                startActivity(Intent(this, MenuActivity::class.java))
            } else {
                // Jika belum login, buka LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            // Tutup SplashActivity
            finish()
        }, 3000) // 3000 milidetik = 3 detik
    }
}
