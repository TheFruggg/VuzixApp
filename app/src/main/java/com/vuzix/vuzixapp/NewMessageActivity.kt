package com.vuzix.vuzixapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        // Generate dummy conversation data
        val conversations = generateDummyConversations()

        // Set up RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewConversations)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ConversationAdapter(conversations)
        recyclerView.adapter = adapter
    }

    private fun generateDummyConversations(): List<Conversation> {
        // Dummy conversation data for testing
        return listOf(
            Conversation("Liam", "Hey, did you know im a cliimber?"),
            Conversation("Douglas", "JAB JAB SLIP SLIP."),
            Conversation("Joseph", "Have you seen the latest movie?"),
            Conversation("Cameron", "Remember to bring the documents."),
            Conversation("Mark", "I'll be there in 10 minutes.")
        )
    }
}
