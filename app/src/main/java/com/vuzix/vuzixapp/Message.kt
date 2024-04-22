package com.vuzix.vuzixapp

import android.service.autofill.FillEventHistory
import com.google.firebase.Timestamp


data class Message(
    val senderId: String,
    val recipientId: String,
    val content: String,
    val history: Int,
    val timestamp: Timestamp
)
