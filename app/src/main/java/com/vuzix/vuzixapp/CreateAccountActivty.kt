package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import okio.Buffer // Import Buffer from okio package

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_create_account)

        val buttonCreateAccount: Button = findViewById(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            generateTokenAndSendRequest()
        }
    }

    private fun generateTokenAndSendRequest() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    val editTextFirstName = findViewById<EditText>(R.id.editTextFirstName)
                    val editTextLastName = findViewById<EditText>(R.id.editTextLastName)
                    val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
                    val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
                    val firstName = editTextFirstName.text.toString()
                    val lastName = editTextLastName.text.toString()
                    val email = editTextEmail.text.toString()
                    val password = editTextPassword.text.toString()

                    // Do something with the token
                    // Show confirmation message
                    Toast.makeText(this, "Token generated successfully: $token", Toast.LENGTH_SHORT).show()
                    // Log token separately
                    Log.d("CreateAccountActivity", "Token: $token")
                    // Log user information
                    Log.d("CreateAccountActivity", "First Name: $firstName, Last Name: $lastName, Email: $email, Password: $password")
                    // Send a POST request to the server
                    sendPostRequest(token, firstName, lastName, email, password)
                    // Finish the activity and navigate back to the login page

                } else {
                    // Handle token generation failure
                    Toast.makeText(this, "Failed to generate token", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendPostRequest(token: String, firstName: String, lastName: String, email: String, password: String) {

        val client = OkHttpClient()
        val url = "https://cypher-text.com:3000/signup"
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), """
        {   
            "token": "$token",
            "firstName": "$firstName",
            "lastName": "$lastName",
            "email": "$email",
            "password": "$password"
        }
        """.trimIndent())



        // Log the request body
        val requestBodyString = requestBodyToString(requestBody)
        Log.d("CreateAccountActivity", "Request Body: $requestBodyString")

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@CreateAccountActivity, "Failed to send request", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("Response", "Response Body: $responseBody")

                // Check the response message
                if (responseBody?.contains("User registered successfully!", ignoreCase = true) == true) {
                    // The response message contains "successful"

                    runOnUiThread {
                        Toast.makeText(
                            this@CreateAccountActivity,
                            "Response: $responseBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    navigateToLogin()

                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@CreateAccountActivity,
                            "Response: $responseBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    // Function to convert RequestBody to String
    private fun requestBodyToString(requestBody: RequestBody): String {
        val buffer = Buffer()
        try {
            requestBody.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            buffer.close()
        }
        return ""
    }
    private fun navigateToLogin() {
        // Code to navigate to MainActivity
        // For example:
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}