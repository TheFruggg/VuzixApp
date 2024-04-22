// MessagesActivity.kt
package com.vuzix.vuzixapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
        Log.d("working messages activity", "its working here 1")
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
        Log.d("working messages activity", "its working here 2")
    }


    private fun fetchMessagesForConversation(conversationId: String) {
        // List to hold all fetched messages
        val messages = mutableListOf<Message>()
        Log.d("working messages activity", "its working here 3")

        // Fetch sent messages with history = 1
        db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .whereEqualTo("senderId", userId ?: "")
            .whereEqualTo("history", 1) // Filter messages by history field set to 1
            .get()
            .addOnSuccessListener { documents ->
                // Iterate through documents to extract sent message data
                documents.forEach { document ->
                    val recipientId = document.getString("recipientId") ?: ""
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
                    val history = document.getLong("history")?.toInt() ?: 0
                    messages.add(Message(userId ?:"", recipientId , content,history, timestamp))
                    //allMessages.add(message)
                    Log.d("working messages activity", "its working here 4")
                }

                // Fetch sent messages with history = 0
                db.collection("conversations")
                    .document(conversationId)
                    .collection("messages")
                    .whereEqualTo("recipientId", userId ?: "")
                    .whereEqualTo("history", 0) // Filter messages by history field set to 0
                    .get()
                    .addOnSuccessListener { documents ->
                        // Iterate through documents to extract sent message data with history = 0
                        documents.forEach { document ->
                            val recipientId = document.getString("recipientId") ?: ""
                            val content = document.getString("content") ?: ""
                            val history = document.getLong("history")?.toInt() ?: 0
                            val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
                            messages.add(Message(recipientId, userId ?:"" , content,history, timestamp))
                        }

                        // Sort messages by timestamp in ascending order
                        messages.sortBy { it.timestamp }

                        // Initialize and set up MessageAdapter with fetched messages

                        messageAdapter = MessageAdapter(messages, userId ?: "")
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
