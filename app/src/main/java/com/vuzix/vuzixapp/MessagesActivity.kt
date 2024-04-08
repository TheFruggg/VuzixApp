package com.vuzix.vuzixapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query

class MessagesActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var editTextMessageReply: EditText
    private lateinit var buttonSendReply: Button
    private lateinit var conversationId: String
    private var recipientId: String? = null
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var refreshHandler: Handler
    private lateinit var refreshRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        // Retrieve conversation and recipient details from intent
        conversationId = intent.getStringExtra("conversationId") ?: ""
        recipientId = intent.getStringExtra("recipientId")

        // Check if conversation details are available
        if (conversationId.isBlank() || recipientId.isNullOrEmpty()) {
            Toast.makeText(this, "Missing conversation details.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views and setup send button
        initViews()
        setupSendButton()
        // Fetch messages for the conversation
        fetchMessagesForConversation(conversationId)

        // Initialize handler and runnable for message refresh every 5 secs
        refreshHandler = Handler(Looper.getMainLooper())
        refreshRunnable = object : Runnable {
            override fun run() {
                fetchMessagesForConversation(conversationId)
                refreshHandler.postDelayed(this, 5000) // Schedule the next run in 5 seconds
            }
        }

        // Start the repeating task
        refreshHandler.post(refreshRunnable)
    }

    // Initialize views
    private fun initViews() {
        messagesRecyclerView = findViewById(R.id.recyclerViewMessages)
        editTextMessageReply = findViewById(R.id.editTextMessageReply)
        buttonSendReply = findViewById(R.id.buttonSendReply)
        layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.layoutManager = layoutManager
    }

    // Setup send button click listener
    private fun setupSendButton() {
        buttonSendReply.setOnClickListener {
            val messageContent = editTextMessageReply.text.toString()
            if (messageContent.isNotBlank()) {
                sendMessage(messageContent)
                editTextMessageReply.text.clear()
            }
        }
    }

    // Fetch messages for the conversation from Firestore
    private fun fetchMessagesForConversation(conversationId: String) {
        db.collection("conversations")
            .document(conversationId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val messages = mutableListOf<Message>()
                documents.forEach { document ->
                    val senderId = document.getString("senderId") ?: ""
                    val content = document.getString("content") ?: ""
                    val timestamp = document.getTimestamp("timestamp") ?: Timestamp.now()
                    messages.add(Message(senderId, userId ?: "", content, timestamp))
                }
                updateUI(messages)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching messages: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Update the UI with fetched messages
    private fun updateUI(messages: List<Message>) {
        messageAdapter = MessageAdapter(messages, userId ?: "")
        messagesRecyclerView.adapter = messageAdapter
        if(messages.isNotEmpty()) messagesRecyclerView.scrollToPosition(messages.size - 1)
    }

    // Send message to the conversation so the user doesn't have to leave this page to send a message
    private fun sendMessage(content: String) {
        val messageMap = hashMapOf(
            "senderId" to userId,
            "recipientId" to recipientId,
            "content" to content,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("conversations").document(conversationId).collection("messages").add(messageMap)
            .addOnSuccessListener {
                fetchMessagesForConversation(conversationId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Handle navigation through messages using the weird ass touch pad on the glasses
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                messagesRecyclerView.smoothScrollBy(0, -layoutManager.height / 4)
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                messagesRecyclerView.smoothScrollBy(0, layoutManager.height / 4)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    // Pause the refresh task when the activity is paused
    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    // Resume the refresh task when the activity is resumed
    override fun onResume() {
        super.onResume()
        refreshHandler.post(refreshRunnable)
    }
}
