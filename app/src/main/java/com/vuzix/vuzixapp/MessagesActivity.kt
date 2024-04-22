package com.vuzix.vuzixapp

import android.os.Bundle
import android.util.Log
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
        recipientId = intent.getStringExtra("recipientId") ?: ""




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
                Log.d("warning warning help", "its working here")
                sendMessage(messageContent)
                editTextMessageReply.text.clear()
            }
        }
    }


    private fun fetchMessagesForConversation(conversationId: String) {
        // List to hold all fetched messages
        val messages = mutableListOf<Message>()


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
                        Log.d("working messages activity", "help help help")
                        messageAdapter = MessageAdapter(messages, userId ?: "")
                        messagesRecyclerView.adapter = messageAdapter
                        updateUI(messages)
                    }
                    .addOnFailureListener { exception ->
                        // Handle errors
                    }
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
        //conversationId = intent.getStringExtra("conversationId") ?: ""



        db.collection("conversations").document(conversationId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Step 2: Get the list of participants
                    val participants = document.get("participants") as? List<String>

                    // Check if the participants list contains the originalUserId
                    participants?.let {
                        // Find the other user
                        val recipientId = it.firstOrNull { id -> id != userId }

                        recipientId?.let { recipientId ->
                            // Step 3: Get the public key of the other user
                            db.collection("users").document(recipientId)
                                .get()
                                .addOnSuccessListener { userDoc ->
                                    val recipientPublicKey = userDoc.getString("Public")

                                    if (recipientPublicKey != null) {
                                        if (userId != null) {
                                            NewMessageActivity().checkConversationExists(userId, recipientId, content, recipientPublicKey )
                                        }
                                    } else {

                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Error retrieving user data: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        } ?: run {
                            Toast.makeText(
                                this,
                                "Other user ID not found in conversation",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } ?: run {
                        Toast.makeText(
                            this,
                            "No participants in conversation",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Conversation not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error retrieving conversation: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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

