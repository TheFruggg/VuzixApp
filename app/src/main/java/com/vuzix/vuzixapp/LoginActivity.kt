package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // Set content view using the layout for login
        setContentView(R.layout.activity_login)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Set click listener for the login button
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            signIn()
        }

        // Set click listener for the create account button
        val buttonCreateAccount = findViewById<Button>(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    // Function to sign in with email and password
    private fun signIn() {
        // Get email and password from EditText fields
        val editTextEmail = findViewById<EditText>(R.id.loginTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.loginTextPassword)
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        // Check if any field is empty
        if (email.isEmpty() || password.isEmpty()) {
            // Display a toast message if any field is empty
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Sign in with email and password using FirebaseAuth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in success, navigate to next activity or perform necessary actions
                    Toast.makeText(this, "Sign-in successful", Toast.LENGTH_SHORT).show()
                    // Example: Navigate to the home activity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign-in fails, display a message to the user.
                    Toast.makeText(this, "Sign-in failed. Invalid email or password.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
