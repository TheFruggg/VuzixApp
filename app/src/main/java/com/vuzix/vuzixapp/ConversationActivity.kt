package com.vuzix.vuzixapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConversationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        recyclerView = findViewById(R.id.recyclerViewConversation)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter()
        recyclerView.adapter = adapter

        val messages = listOf(
            Message("Hello", true),
            Message("Hi there!", false),
            Message("How are you?", true),
            Message("I'm good, thank you!", false)
        )

        adapter.submitList(messages)
    }
}
