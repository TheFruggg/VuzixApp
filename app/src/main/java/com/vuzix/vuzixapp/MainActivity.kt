package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = listOf(
            Option("New Message") { navigateTo(NewMessageActivity::class.java) },
            Option("Contacts") { navigateTo(ContactsActivity::class.java) },
            Option("Settings") { navigateTo(SettingsActivity::class.java) },
            Option("Exit") { finish() }
        )

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = OptionAdapter(options)
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
