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

    // Function to fetch messages for a conversation from Firestore
    private fun fetchMessagesForConversation(conversationId: String) {
        db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING) // Order messages by timestamp in ascending order
            .get()
            .addOnSuccessListener { documents ->
                // List to hold fetched messages
                val messages = mutableListOf<Message>()

                // Iterate through documents to extract message data
                for (document in documents) {
                    val senderId = document.getString("senderId") ?: ""
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
                    val message = Message(senderId, userId ?: "", content, timestamp)
                    messages.add(message)
                }

                // Initialize and set up MessageAdapter with fetched messages
                messageAdapter = MessageAdapter(messages, userId ?: "")
                messagesRecyclerView.adapter = messageAdapter
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}
