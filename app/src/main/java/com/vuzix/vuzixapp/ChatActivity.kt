package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    // Initialize Firebase Firestore and FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private val conversations = mutableListOf<Conversation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize RecyclerView for displaying conversations
        conversationsRecyclerView = findViewById(R.id.recyclerViewConversations)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up RecyclerView adapter with click listener to navigate to MessagesActivity
        conversationAdapter = ConversationAdapter(conversations) { conversation ->
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra("conversationId", conversation.conversationId)
            startActivity(intent)
        }
        conversationsRecyclerView.adapter = conversationAdapter

        // Fetch conversations for the current user
        fetchConversationsForCurrentUser()
    }

    // Fetch conversations for the current user from Firestore
    private fun fetchConversationsForCurrentUser() {
        if (userId == null) {
            return // Exit function if user ID is null
        }

        // Query Firestore to retrieve conversations where the user is a participant
        db.collection("conversations")
            .whereArrayContains("participants", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // Extract conversation ID and participants from Firestore document
                    val conversationId = document.id
                    val participants = document.get("participants") as? List<String> ?: emptyList()
                    val conversation = Conversation(conversationId, participants)
                    conversations.add(conversation) // Add conversation to the list
                }
                conversationAdapter.notifyDataSetChanged() // Notify adapter of dataset change
            }
            .addOnFailureListener { exception ->
                // Handle failures, e.g., network issues, permission denied
            }
    }
}
