package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.vuzix.vuzixapp.KeyActivity
import android.view.View

// Define MainActivity class, extending AppCompatActivity
class MainActivity : AppCompatActivity() {

    // Override onCreate method to initialize the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the navigation bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        // Set the layout for this activity
        setContentView(R.layout.activity_main)

        // Define a list of options to display in the ViewPager
        val options = listOf(
            Option("New Message") { navigateTo(MessageActivity::class.java) }, // Navigate to MessageActivity
            Option("Contacts") { navigateTo(ContactsActivity::class.java) }, // Navigate to ContactsActivity
            Option("Settings") { navigateTo(SettingsActivity::class.java) }, // Navigate to SettingsActivity
            Option("Test") { KeyActivity().GenerateKeyPair() },
            Option("Exit") { finish() } // Exit the application
        )

        // Find the ViewPager view in the layout and assign it to viewPager variable
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // Set the adapter for the ViewPager
        viewPager.adapter = OptionAdapter(options)

        // Set the orientation of the ViewPager as horizontal
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        // Set the offscreen page limit for the ViewPager
        viewPager.offscreenPageLimit = 1
    }

    // Function to navigate to another activity
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
