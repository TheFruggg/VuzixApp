package com.vuzix.vuzixapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

// Define ContactSettings class, which extends AppCompatActivity
class ContactSettings : AppCompatActivity() {

    // Override the onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_contacts)

        // Define options list containing Option objects
        val options = listOf(
            Option("Notifications") { /* Action for Notifications */ }, // Option for Notifications
            Option("Edit Name") { /* Action for Edit Name */ }, // Option for Edit Name
            Option("Delete Contact") { /* Action for Delete Contact */ } // Option for Delete Contact
        )

        // Find the ViewPager2 view in the layout and assign it to viewPager variable
        val viewPager: ViewPager2 = findViewById(R.id.viewPagerContacts)

        // Set the adapter for the viewPager with OptionAdapter containing options list
        viewPager.adapter = OptionAdapter(options)
    }
}
