package com.vuzix.vuzixapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val options = listOf(
            Option("Conversation 1") { navigateToConversation("Conversation 1") },
            Option("Conversation 2") { navigateToConversation("Conversation 2") },
            Option("Conversation 3") { navigateToConversation("Conversation 3") },
            Option("Conversation 4") { navigateToConversation("Conversation 4") },
            Option("Conversation 5") { navigateToConversation("Conversation 5") }
        )

        val viewPager: ViewPager2 = findViewById(R.id.viewPagerContacts)
        viewPager.adapter = OptionAdapter(options)
    }

    private fun navigateToConversation(conversation: String) {
        val intent = Intent(this, ConversationActivity::class.java).apply {
            putExtra("conversation_key", conversation)
        }
        startActivity(intent)
    }
}
