package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class MenuActivity : AppCompatActivity() {

    private lateinit var toMainActivityButton: LinearLayout
    private lateinit var toMainActivity2Button: LinearLayout
    private lateinit var toMainActivity3Button: LinearLayout
    private lateinit var logoutButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_menu)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        val username = sharedPreferences.getString("username", "User")

        val userNameTextView: TextView = findViewById(R.id.userName)
        userNameTextView.text = username

        

        toMainActivityButton = findViewById(R.id.mainActivityButtonContainer)
        toMainActivity2Button = findViewById(R.id.mainActivity2ButtonContainer)
        toMainActivity3Button = findViewById(R.id.mainActivity3ButtonContainer)
        logoutButton = findViewById(R.id.logoutButtonContainer)

        toMainActivityButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        toMainActivity2Button.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        toMainActivity3Button.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.putBoolean("logged_in", false)
            editor.remove("username")
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
