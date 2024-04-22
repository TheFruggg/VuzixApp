package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar and make the activity fullscreen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // Set content view using the layout for the main activity
        setContentView(R.layout.activity_main)

        // Define options for the menu
        val options = listOf(
            "New Message", "Chats", "Exit"
        )

        // Initialize and set up RecyclerView for displaying menu options horizontally
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Set adapter for the RecyclerView with a click listener
        recyclerView.adapter = MenuAdapter(options) { position ->
            // Navigate to different activities based on the selected option
            when (position) {
                0 -> navigateTo(NewMessageActivity::class.java)
                1 -> navigateTo(ChatActivity::class.java)
                2 -> finish() // Exit the application
            }
        }
    }

    // Function to navigate to a specified activity class
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
