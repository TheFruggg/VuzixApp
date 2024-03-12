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
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setContentView(R.layout.activity_main)

        val options = listOf(
            "New Message", "Chats", "Contacts", "Settings", "Test", "Exit"
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = MenuAdapter(options) { position ->
            when (position) {
                0 -> navigateTo(NewMessageActivity::class.java)
                1 -> navigateTo(ChatActivity::class.java)
                2 -> navigateTo(ContactsActivity::class.java)
                3 -> navigateTo(SettingsActivity::class.java)
                4 -> KeyActivity().GenerateKeyPair()
                5 -> finish()
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
