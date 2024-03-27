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
    private var highlightedPosition = 0 // Initially no item is selected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        conversationsRecyclerView = findViewById(R.id.recyclerViewConversations)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(this)

        conversationAdapter = ConversationAdapter(conversations) { conversation ->
            val otherUserId = conversation.participants.firstOrNull { it != userId }
            val intent = Intent(this, MessagesActivity::class.java).apply {
                putExtra("conversationId", conversation.conversationId)
                putExtra("recipientId", otherUserId)
            }
            startActivity(intent)
        }
        conversationsRecyclerView.adapter = conversationAdapter

        fetchConversationsForCurrentUser()
    }

    private fun fetchConversationsForCurrentUser() {
        userId?.let {
            db.collection("conversations")
                .whereArrayContains("participants", it)
                .get()
                .addOnSuccessListener { documents ->
                    conversations.clear()
                    documents.forEach { document ->
                        val conversationId = document.id
                        val participants = document.get("participants") as List<String>
                        conversations.add(Conversation(conversationId, participants))
                    }
                    conversationAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace() // Handle the error appropriately
                }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                highlightedPosition = (highlightedPosition + 1) % conversations.size
                conversationAdapter.selectItem(highlightedPosition)
                conversationsRecyclerView.scrollToPosition(highlightedPosition)
                return true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                highlightedPosition = if (highlightedPosition > 0) highlightedPosition - 1 else conversations.size - 1
                conversationAdapter.selectItem(highlightedPosition)
                conversationsRecyclerView.scrollToPosition(highlightedPosition)
                return true
            }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                conversations.getOrNull(highlightedPosition)?.let { selectedConversation ->
                    val intent = Intent(this, MessagesActivity::class.java).apply {
                        putExtra("conversationId", selectedConversation.conversationId)
                        putExtra("recipientId", selectedConversation.participants.firstOrNull { it != userId })
                    }
                    startActivity(intent)
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
