package com.vuzix.vuzixapp

import com.google.firebase.Timestamp


data class Message(
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: Timestamp
)
