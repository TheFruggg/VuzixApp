package com.vuzix.vuzixapp

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        conversationId = intent.getStringExtra("conversationId") ?: ""
        recipientId = intent.getStringExtra("recipientId")

        if (conversationId.isBlank() || recipientId.isNullOrEmpty()) {
            Toast.makeText(this, "Missing conversation details.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        messagesRecyclerView = findViewById(R.id.recyclerViewMessages)
        editTextMessageReply = findViewById(R.id.editTextMessageReply)
        buttonSendReply = findViewById(R.id.buttonSendReply)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)

        buttonSendReply.setOnClickListener {
            val messageContent = editTextMessageReply.text.toString()
            if (messageContent.isNotBlank()) {
                sendMessage(messageContent)
                editTextMessageReply.text.clear()
            }
        }

        fetchMessagesForConversation(conversationId)
    }

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
                messageAdapter = MessageAdapter(messages, userId ?: "")
                messagesRecyclerView.adapter = messageAdapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching messages: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendMessage(content: String) {
        val messageMap = hashMapOf(
            "senderId" to userId,
            "recipientId" to recipientId,
            "content" to content,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("conversations").document(conversationId).collection("messages").add(messageMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
