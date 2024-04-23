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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            signIn()
        }

        val buttonCreateAccount = findViewById<Button>(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }

    private fun signIn() {
        val editTextEmail = findViewById<EditText>(R.id.loginTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.loginTextPassword)
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if (auth.currentUser?.isEmailVerified == true) {
                    // Email is verified, proceed to the main activity
                    Toast.makeText(this, "Sign-in successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Email is not verified, prompt the user to check their inbox
                    Toast.makeText(this, "Please verify your email first. Check your inbox.", Toast.LENGTH_LONG).show()
                    auth.signOut()  // Log out the user to prevent unauthorized access until they verify their email
                }
            } else {
                // Authentication failed, show the user an error message
                Toast.makeText(this, "Sign-in failed. Invalid email or password.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
