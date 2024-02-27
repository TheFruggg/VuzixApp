package com.vuzix.vuzixapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Define ConversationActivity class, extending AppCompatActivity
class ConversationActivity : AppCompatActivity() {

    // Declare variables for RecyclerView and MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter

    // Override onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        // Set the layout for this activity
        setContentView(R.layout.activity_conversation)

        // Find the RecyclerView view in the layout and assign it to recyclerView variable
        recyclerView = findViewById(R.id.recyclerViewConversation)

        // Set the layout manager for the RecyclerView as LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the MessageAdapter
        adapter = MessageAdapter()

        // Set the adapter for the RecyclerView
        recyclerView.adapter = adapter

        // Define a list of messages to display in the RecyclerView
        val messages = listOf(
            Message("Hello", true), // Sent message
            Message("Hi there!", false), // Received message
            Message("How are you?", true), // Sent message
            Message("I'm good, thank you!", false) // Received message
        )

        // Submit the list of messages to the adapter for display
        adapter.submitList(messages)
    }
}
