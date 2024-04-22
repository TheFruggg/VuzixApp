package com.vuzix.vuzixapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.crypto.SecretKey


import java.security.KeyFactory

import java.security.spec.X509EncodedKeySpec

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.Signature
import java.util.Base64
import javax.crypto.Cipher

class NewMessageActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val SYMMETRIC_KEY_ALIAS = "MySymmetricKeyAlias"
    private val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_new_message)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize UI elements
        val editTextRecipient: EditText = findViewById(R.id.editTextRecipient)
        val editTextMessage: EditText = findViewById(R.id.editTextMessage)
        val buttonSend: Button = findViewById(R.id.buttonSend)

        // Set OnClickListener for the send button
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
                // User is not logged in, handle appropriately
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lookup recipient user ID in Firestore
            db.collection("users")
                .whereEqualTo("email", recipientEmail)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val recipientPublicKey = document.getString("Public") ?: ""
                        val recipientId = document.id
                        checkConversationExists(senderId, recipientId, messageContent, recipientPublicKey)
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

    // Function to check if conversation exists and create it if necessary
    private fun checkConversationExists(senderId: String, recipientId: String, messageContent: String, recipientPublicKey: String) {
        // Fetch all conversations for the sender
        db.collection("conversations")
            .whereArrayContains("participants", senderId)
            .get()
            .addOnSuccessListener { senderConversations ->
                // Filter sender's conversations based on recipient
                val matchingConversation = senderConversations.documents.find { conversationDoc ->
                    val participants = conversationDoc.get("participants") as? List<String>
                    participants?.contains(recipientId) == true
                }

                if (matchingConversation != null) {
                    // Conversation already exists, add message to it
                    val conversationId = matchingConversation.id
                    addMessageToConversation(conversationId, senderId, recipientId, messageContent,recipientPublicKey)
                    addMessageToHistory(conversationId, senderId, recipientId, messageContent)
                } else {
                    // Conversation doesn't exist, create a new one
                    createConversation(senderId, recipientId, messageContent, recipientPublicKey)
                }
            }
            .addOnFailureListener { e ->
                // Failed to fetch sender's conversations
                Toast.makeText(this, "Failed to fetch conversations: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to create a new conversation
    private fun createConversation(senderId: String, recipientId: String, messageContent: String, recipientPublicKey: String) {
        val conversationData = hashMapOf(
            "participants" to arrayListOf(senderId, recipientId)
        )

        db.collection("conversations")
            .add(conversationData)
            .addOnSuccessListener { documentReference ->
                // Conversation created successfully, now add message to it
                val conversationId = documentReference.id
                addMessageToConversation(conversationId, senderId, recipientId, messageContent, recipientPublicKey)
                addMessageToHistory(conversationId, senderId, recipientId, messageContent)
            }
            .addOnFailureListener { e ->
                // Failed to create conversation
                Toast.makeText(this, "Failed to create conversation: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to add message to an existing conversation
    fun addMessageToConversation(conversationId: String, senderId: String, recipientId: String, messageContent: String, publicKeyString : String) {
        val db = FirebaseFirestore.getInstance()
        val messagesRef = db.collection("conversations").document(conversationId).collection("messages")
        val history = 0
        Log.d("public key", "public key before decoding: ${publicKeyString}")
        val publicKeyStringWithoutNewlines = publicKeyString.replace("\n", "")
        val decodedKey = android.util.Base64.decode(publicKeyStringWithoutNewlines, android.util.Base64.DEFAULT)

        val keyFactory = java.security.KeyFactory.getInstance("RSA")
        val recipientPublicKey = keyFactory.generatePublic(X509EncodedKeySpec(decodedKey))
        Log.d("public key", "public key after decoding: ${recipientPublicKey}")
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey)
        val encryptedBytes = cipher.doFinal(messageContent.toByteArray())
        val encryptedMessage = android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)



        // Return the encrypted message as Base64 encoded string



        val message = hashMapOf(
            "senderId" to senderId,
            "recipientId" to recipientId,
            "content" to encryptedMessage,
            "history" to history,
            "timestamp" to FieldValue.serverTimestamp() // Use server timestamp

        )

        messagesRef.add(message)
            .addOnSuccessListener { documentReference ->
                // Message sent successfully
                //Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                // Optionally, clear the message input field or navigate to another screen
            }
            .addOnFailureListener { e ->
                // Failed to send message
                //Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun addMessageToHistory(conversationId: String, senderId: String, recipientId: String, messageContent: String) {
        val db = FirebaseFirestore.getInstance()
        val messagesRef = db.collection("conversations").document(conversationId).collection("messages")
        val history = 1

        val secretKey = retrieveSymmetricKey()
        //Log.d("public key", "public key before decoding: ${publicKeyString}")

        if (secretKey != null) {
            val encryptedBytes = encryptMessage(messageContent,secretKey)
            val encryptedMessage = android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
            val message = hashMapOf(
                "senderId" to senderId,
                "recipientId" to recipientId,
                "content" to encryptedMessage,
                "history" to history,
                "timestamp" to FieldValue.serverTimestamp() // Use server timestamp
            )

            messagesRef.add(message)
                .addOnSuccessListener { documentReference ->
                    // Message sent successfully
                    //Toast.makeText(this, "Message sent successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, clear the message input field or navigate to another screen
                }
                .addOnFailureListener { e ->
                    // Failed to send message
                    //Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }



        // Return the encrypted message as Base64 encoded string




    }
    fun encryptMessage(message: String, secretKey: SecretKey): ByteArray {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)

            val iv = cipher.iv // Get the initialization vector
            val encryptedBytes = cipher.doFinal(message.toByteArray())

            // Return IV + encrypted message
            return iv + encryptedBytes

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
    // Function to validate email format
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    fun retrieveSymmetricKey(): SecretKey? {

        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER)
            keyStore.load(null)

            // Check if the symmetric key with the given alias exists
            if (keyStore.containsAlias(SYMMETRIC_KEY_ALIAS)) {
                // Retrieve the symmetric key from the keystore
                val keyEntry = keyStore.getEntry(SYMMETRIC_KEY_ALIAS, null) as KeyStore.SecretKeyEntry
                return keyEntry.secretKey
            }

            return null // Symmetric key not found

        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}

