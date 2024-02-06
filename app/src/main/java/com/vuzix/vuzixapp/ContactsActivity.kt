package com.vuzix.vuzixapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val options = listOf(
            Option("Liam") { /* Action for Liam */ },
            Option("Douglas") { /* Action for Douglas */ },
            Option("Joseph") { /* Action for Joseph */ },
            Option("Cameron") { /* Action for Cameron */ },
            Option("Mark") { /* Action for Mark */ }
        )

        val viewPager: ViewPager2 = findViewById(R.id.viewPagerContacts)
        viewPager.adapter = OptionAdapter(options)
    }
}
