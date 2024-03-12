// In your SearchAddContactActivity.kt
package com.vuzix.vuzixapp
// Import necessary libraries
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
import java.io.IOException
import okhttp3.*
import okio.Buffer


class SearchAddContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the notification and navigation bars
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_search_add_contact)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            searchContact()
        }
    }

    private fun searchContact(){
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val email = editTextEmail.text.toString()
        RequestKey(email)
    }
    private fun RequestKey(email: String) {
        val client = OkHttpClient()
        val url = "https://cypher-text.com:3000/key"
        val requestBody = RequestBody.create(
            "application/json".toMediaTypeOrNull(), """
        {
            "email": "$email"
            
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
                            this@SearchAddContactActivity,
                            "Failed to send request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("Response", "Response Body: $responseBody")

                    // Check the HTTP status code for success
                    if (response.code == 200) {
                        runOnUiThread {
                            Toast.makeText(
                                this@SearchAddContactActivity,
                                "Public key was found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        navigateToMainActivity()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@SearchAddContactActivity,
                                "Error: ${response.code}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        // Handle error response based on response code or response body
                        //handleErrorResponse(responseBody)
                    }
                }

                // Function to handle specific error responses based on the response body
                private fun handleErrorResponse(responseBody: String?) {
                    // Implement logic to handle specific error responses
                    if (responseBody?.contains("Invalid credentials", ignoreCase = true) == true) {
                        // Handle invalid credentials error
                        // For example, show an error message to the user
                        runOnUiThread {
                            Toast.makeText(
                                this@SearchAddContactActivity,
                                "Invalid credentials. Please check your username and password.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle other error scenarios
                        // ...
                    }
                }
            })

        }  catch (e: Exception) {
            // Handle exceptions
            Log.e("Error", "Error occurred: ${e.message}", e)
            runOnUiThread {
                Toast.makeText(
                    this@SearchAddContactActivity,
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