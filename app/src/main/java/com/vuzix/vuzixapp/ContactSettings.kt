package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

// Define ContactSettings class, which extends AppCompatActivity
class ContactSettings : AppCompatActivity() {

    // Override the onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        // Set the layout for this activity
        setContentView(R.layout.activity_contacts)

        // Define options list containing Option objects
        val options = mutableListOf(
            Option("New Message")  { navigateTo(NewMessageActivity::class.java) }, // Option for New Message
            Option("Notifications") { /* Action for Notifications */ }, // Option for Notifications
            Option("Edit Name") { /* Action for Edit Name */ }, // Option for Edit Name
            Option("Delete Contact") { /* Action for Delete Contact */ } // Option for Delete Contact
        )

        // Find the ViewPager2 view in the layout and assign it to viewPager variable
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerContacts)

        // Set the adapter for the viewPager with OptionAdapter containing options list
        viewPager.adapter = OptionAdapter(options)
    }

    // Define a function navigateTo which takes a destination Class as parameter
    private fun navigateTo(destination: Class<*>) {
        // Create an Intent to navigate to the provided destination
        val intent = Intent(this, destination)
        // Start the activity using the intent
        startActivity(intent)
    }
}
