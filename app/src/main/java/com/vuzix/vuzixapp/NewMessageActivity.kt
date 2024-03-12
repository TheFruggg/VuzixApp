package com.vuzix.vuzixapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class NewMessageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_new_message)

        auth = FirebaseAuth.getInstance()

        val editTextRecipient: EditText = findViewById(R.id.editTextRecipient)
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val buttonSend: Button = findViewById(R.id.buttonSend)

        buttonSend.setOnClickListener {
            val recipientEmail = editTextRecipient.text.toString()
            val messageContent = editTextMessage.text.toString()

            // Validate recipient email format
            if (!isValidEmail(recipientEmail)) {
                Toast.makeText(this, "Invalid recipient email format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get logged-in user's ID as the sender ID
            val senderId = auth.currentUser?.uid
            if (senderId == null) {
                // User is not logged in, handle appropriately (redirect to login page, etc.)
                // For simplicity, let's just display a toast message
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lookup recipient user ID in Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .whereEqualTo("email", recipientEmail)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val recipientId = document.id
                        sendMessage(senderId, recipientId, messageContent)
                        return@addOnSuccessListener
                    }
                    // Handle case where recipient email doesn't match any user
                    Toast.makeText(this, "Recipient email not found", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    // Handle error
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun sendMessage(senderId: String, recipientId: String, messageContent: String) {
        val db = FirebaseFirestore.getInstance()
        val messagesRef = db.collection("messages")

        val message = hashMapOf(
            "senderId" to senderId,
            "recipientId" to recipientId,
            "content" to messageContent,
            "timestamp" to FieldValue.serverTimestamp() // Use server timestamp
        )

        messagesRef.add(message)
            .addOnSuccessListener { documentReference ->
                // Message sent successfully
                Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                // Optionally, clear the message input field or navigate to another screen
            }
            .addOnFailureListener { e ->
                // Failed to send message
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
