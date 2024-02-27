// LoginActivity.kt
package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Hide the navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            // Handle login logic here
            // For testing purposes, just navigate to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Close the login activity
        }



        val buttonCreateAccount = findViewById<Button>(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            // Navigate to CreateAccountActivity
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }
}