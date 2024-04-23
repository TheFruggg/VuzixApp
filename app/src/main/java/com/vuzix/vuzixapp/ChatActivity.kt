package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
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

        conversationsRecyclerView = findViewById(R.id.recyclerViewConversations)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(this)
        conversationAdapter = ConversationAdapter(conversations) { conversation ->
            navigateToMessages(conversation)
        }
        conversationsRecyclerView.adapter = conversationAdapter
        fetchConversationsForCurrentUser()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                selectPrevious()
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                selectNext()
                return true
            }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                // This assumes 'enter' or 'center' key is your select button
                performSelection()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun performSelection() {
        if (conversationAdapter.selectedPosition != RecyclerView.NO_POSITION) {
            val selectedConversation = conversations[conversationAdapter.selectedPosition]
            navigateToMessages(selectedConversation)
        }
    }

    private fun selectNext() {
        val nextPos = if (conversationAdapter.selectedPosition == RecyclerView.NO_POSITION) 0
        else (conversationAdapter.selectedPosition + 1) % conversationAdapter.itemCount
        updateSelectedPosition(nextPos)
    }

    private fun selectPrevious() {
        val prevPos = if (conversationAdapter.selectedPosition == RecyclerView.NO_POSITION || conversationAdapter.selectedPosition == 0) 0
        else conversationAdapter.selectedPosition - 1
        updateSelectedPosition(prevPos)
    }

    private fun updateSelectedPosition(position: Int) {
        conversationAdapter.selectedPosition = position
        conversationAdapter.notifyDataSetChanged()
        conversationsRecyclerView.scrollToPosition(position)
    }

    private fun navigateToMessages(conversation: Conversation) {
        val intent = Intent(this, MessagesActivity::class.java).apply {
            putExtra("conversationId", conversation.conversationId)
        }
        startActivity(intent)
    }

    private fun fetchConversationsForCurrentUser() {
        if (userId == null) return
        db.collection("conversations").whereArrayContains("participants", userId).get()
            .addOnSuccessListener { documents ->
                conversations.clear()  // Clear existing data to avoid duplicating items
                for (document in documents) {
                    val conversationId = document.id
                    val participants = document.get("participants") as? List<String> ?: emptyList()
                    conversations.add(Conversation(conversationId, participants))
                }
                conversationAdapter.notifyDataSetChanged()
                // Initialize the selection
                if (conversations.isNotEmpty()) updateSelectedPosition(0)
            }
            .addOnFailureListener {
                // Handle failures
            }
    }
}
