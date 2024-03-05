// LoginActivity.kt
package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_login)



        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            // Handle login logic here
            val loginTextEmail = findViewById<EditText>(R.id.loginTextEmail)
            val loginTextPassword = findViewById<EditText>(R.id.loginTextPassword)
            val loginEmail = loginTextEmail.text.toString()
            val loginPassword = loginTextPassword.text.toString()
            loginPostRequest(loginEmail, loginPassword)

            // For testing purposes, just navigate to MainActivity
            //startActivity(Intent(this, MainActivity::class.java))
            //finish() // Close the login activity

        }



        val buttonCreateAccount = findViewById<Button>(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener {
            // Navigate to CreateAccountActivity
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }
    private fun loginPostRequest(email: String, password: String) {
        val client = OkHttpClient()
        val url = "https://cypher-text.com:3000/login"
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), """
        {
            "email": "$email",
            "password": "$password"
        }
        """.trimIndent())
        // Log the request body
        val requestBodyString = requestBodyToString(requestBody)
        Log.d("LoginActivity", "Request Body: $requestBodyString")

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            // Execute the request asynchronously
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // Handle failure, e.g., network issues
                    Log.e("Error", "Error occurred: ${e.message}", e)
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Failed to send request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("Response", "Response Body: $responseBody")

                    // Check the response message
                    if (responseBody?.contains("Login successful", ignoreCase = true) == true) {
                        // The response message contains "successful"
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "Response: $responseBody",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        navigateToMainActivity()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "Response: $responseBody",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })

    }  catch (e: Exception) {
        // Handle exceptions
        Log.e("Error", "Error occurred: ${e.message}", e)
            runOnUiThread {
                Toast.makeText(
                    this@LoginActivity,
                    "Error has occurred",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
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
    private fun navigateToMainActivity() {
        // Code to navigate to MainActivity
        // For example:
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
