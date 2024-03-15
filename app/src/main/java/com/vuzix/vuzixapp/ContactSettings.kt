package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set system UI to full screen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // Set content view using the same layout as MainActivity
        setContentView(R.layout.activity_main)

        // Define options for the menu (contact settings)
        val options = listOf(
            "New Message", "Notifications", "Edit Name", "Delete Contact"
        )

        // Initialize and set up RecyclerView for displaying contact settings horizontally
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Set adapter for the RecyclerView with a click listener
        recyclerView.adapter = MenuAdapter(options) { position ->
            // Navigate to different activities based on the selected option
            when (position) {
                0 -> navigateTo(NewMessageActivity::class.java)
                // Add more cases for other menu options if needed
            }
        }
    }

    // Function to navigate to a specified activity class
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
