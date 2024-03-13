package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private val conversations = mutableListOf<Conversation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Initialize RecyclerView for conversations
        conversationsRecyclerView = findViewById(R.id.recyclerViewConversations)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set up RecyclerView adapter
        conversationAdapter = ConversationAdapter(conversations) { conversation ->
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra("conversationId", conversation.conversationId)
            startActivity(intent)
        }
        conversationsRecyclerView.adapter = conversationAdapter

        // Fetch conversations for the current user
        fetchConversationsForCurrentUser()
    }

    private fun fetchConversationsForCurrentUser() {
        if (userId == null) {
            return
        }

        db.collection("conversations")
            .whereArrayContains("participants", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val conversationId = document.id
                    val participants = document.get("participants") as? List<String> ?: emptyList()
                    val conversation = Conversation(conversationId, participants)
                    conversations.add(conversation)
                }
                conversationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
}
