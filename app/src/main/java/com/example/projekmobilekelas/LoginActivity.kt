package com.example.projekmobilekelas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.chatapp.DatabaseHelperFirebase

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelperFirebase
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelperFirebase(this)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Perform login using Firebase
            databaseHelper.loginUser(username, password) { success ->
                if (success) {
                    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("logged_in", true)
                    editor.putString("username", username)
                    editor.apply()

                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login gagal!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navigate to RegisterActivity when registerButton is clicked
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
