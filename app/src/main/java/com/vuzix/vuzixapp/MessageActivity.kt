package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Use the same layout as MainActivity

        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // List of conversation options
        val options = listOf(
            "New Message", "Conversation 1", "Conversation 2",
            "Conversation 3", "Conversation 4", "Conversation 5"
        )

        // Setting up RecyclerView with options adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = MenuAdapter(options) { position ->
            when (position) {
                0 -> navigateToContacts() // New Message
                //else -> navigateToConversation("Conversation ${position + 1}")
            }
        }
    }

    // Helper function to navigate to ContactsActivity for new message
    private fun navigateToContacts() {
        val intent = Intent(this, ContactsActivity::class.java)
        startActivity(intent)
    }

    // Helper function to navigate to a specific conversation
    //private fun navigateToConversation(conversation: String) {
    //    val intent = Intent(this, ConversationActivity::class.java).apply {
    //        putExtra("conversation_key", conversation)
    //    }
    //    startActivity(intent)
    //}
}
