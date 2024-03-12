package com.vuzix.vuzixapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize RecyclerView for messages
        messagesRecyclerView = findViewById(R.id.recyclerViewMessages)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up RecyclerView adapter
        messageAdapter = MessageAdapter(messages)
        messagesRecyclerView.adapter = messageAdapter

        // Fetch messages for the current user
        fetchMessagesForCurrentUser()
    }

    private fun fetchMessagesForCurrentUser() {
        db.collection("messages")
            .whereEqualTo("recipientId", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val senderId = document.getString("senderId") ?: ""
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getTimestamp("timestamp")?.toDate()?.toString() ?: ""
                    val isReceived = true // Since we are fetching messages received by the current user
                    val message = Message(senderId, userId!!, content, timestamp)

                    messages.add(message)
                }
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}
