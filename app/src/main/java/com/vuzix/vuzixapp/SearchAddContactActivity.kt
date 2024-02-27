// In your SearchAddContactActivity.kt
package com.vuzix.vuzixapp
// Import necessary libraries
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class SearchAddContactActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the notification and navigation bars
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        setContentView(R.layout.activity_search_add_contact)
    }
}
