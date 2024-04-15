// MessagesActivity.kt
package com.vuzix.vuzixapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class MessagesActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content view using the layout for messages activity
        setContentView(R.layout.activity_messages)

        // Get conversation ID passed from intent
        val conversationId = intent.getStringExtra("conversationId")

        // Check if conversation ID is null
        if (conversationId == null) {
            // Handle error or navigate back
            finish()
            return
        }

        // Initialize RecyclerView for messages
        messagesRecyclerView = findViewById(R.id.recyclerViewMessages)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch messages for the current conversation
        fetchMessagesForConversation(conversationId)
    }


    private fun fetchMessagesForConversation(conversationId: String) {
        // List to hold all fetched messages
        val allMessages = mutableListOf<Message>()

        // Fetch sent messages with history = 1
        db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .whereEqualTo("senderId", userId) // Filter messages by senderId (current user)
            .whereEqualTo("history", 1) // Filter messages by history field set to 1
            .orderBy("timestamp", Query.Direction.ASCENDING) // Order messages by timestamp in ascending order
            .get()
            .addOnSuccessListener { documents ->
                // Iterate through documents to extract sent message data
                for (document in documents) {
                    val recipientId = document.getString("recipientId") ?: ""
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
                    val message = Message(userId ?: "",recipientId, content, timestamp)
                    allMessages.add(message)
                }

                // Fetch received messages with history = 0
                db.collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .whereNotEqualTo("senderId", userId) // Filter messages by senderId not equal to current user
                    .whereEqualTo("history", 0) // Filter messages by history field set to 0
                    .orderBy("timestamp", Query.Direction.ASCENDING) // Order messages by timestamp in ascending order
                    .get()
                    .addOnSuccessListener { documents ->
                        // Iterate through documents to extract received message data
                        for (document in documents) {
                            val senderId = document.getString("senderId") ?: ""
                            val content = document.getString("content") ?: ""
                            val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()

                            val message = Message(senderId, userId ?: "", content, timestamp)
                            allMessages.add(message)
                        }

                        // Sort allMessages by timestamp in ascending order
                        allMessages.sortBy { it.timestamp }

                        // Initialize and set up MessageAdapter with fetched messages
                        messageAdapter = MessageAdapter(allMessages, userId ?: "")
                        messagesRecyclerView.adapter = messageAdapter
                    }
                    .addOnFailureListener { exception ->
                        // Handle errors
                    }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}
