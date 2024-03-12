package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_create_account)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        val buttonCreateAccount: Button = findViewById(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val editTextFirstName = findViewById<EditText>(R.id.editTextFirstName)
        val editTextLastName = findViewById<EditText>(R.id.editTextLastName)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val firstName = editTextFirstName.text.toString()
        val lastName = editTextLastName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-up success, store user info in Firestore
                    val user = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email
                    )

                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                // User info stored successfully in Firestore
                                Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
                                navigateToLogin()
                            }
                            .addOnFailureListener { e ->
                                // Failed to store user info in Firestore
                                Toast.makeText(this, "Failed to create user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If sign-up fails, display a message to the user.
                    Toast.makeText(this, "Sign-up failed. Try again later.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLogin() {
        // Code to navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close the current activity
    }
}
