package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

// Define ContactsActivity class, which extends AppCompatActivity
class ContactsActivity : AppCompatActivity() {

    // Override the onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout for this activity
        setContentView(R.layout.activity_contacts)

        // Define options list containing Option objects
        val options = listOf(
            Option("Liam") { navigateTo(ContactSettings::class.java) }, // Option for Liam
            Option("Douglas") { navigateTo(ContactSettings::class.java) }, // Option for Douglas
            Option("Joseph") { navigateTo(ContactSettings::class.java) }, // Option for Joseph
            Option("Cameron") { navigateTo(ContactSettings::class.java) }, // Option for Cameron
            Option("Mark") { navigateTo(ContactSettings::class.java) } // Option for Mark
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
