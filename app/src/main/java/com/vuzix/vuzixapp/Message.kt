package com.vuzix.vuzixapp

data class Message(
    val senderId: String,
    val recipientId: String,
    val content: String,
    val timestamp: String
)
