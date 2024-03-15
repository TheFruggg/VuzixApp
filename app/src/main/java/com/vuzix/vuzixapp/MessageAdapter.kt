// MessageAdapter.kt
package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vuzix.vuzixapp.Message
import com.vuzix.vuzixapp.R

class MessageAdapter(private val messages: List<Message>, private val currentUserId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Constants for message types
    private val RECEIVED_MESSAGE = 0
    private val SENT_MESSAGE = 1

    // ViewHolder for received messages
    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewReceivedMessage: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
    }

    // ViewHolder for sent messages
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSentMessage: TextView = itemView.findViewById(R.id.textViewSentMessage)
    }

    // Create ViewHolder based on message type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == RECEIVED_MESSAGE) {
            // Inflate layout for received message and return ReceivedMessageViewHolder
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        } else {
            // Inflate layout for sent message and return SentMessageViewHolder
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        }
    }

    // Bind message data to ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder.itemViewType == RECEIVED_MESSAGE) {
            // Bind received message content to ReceivedMessageViewHolder
            val receivedHolder = holder as ReceivedMessageViewHolder
            receivedHolder.textViewReceivedMessage.text = message.content
        } else {
            // Bind sent message content to SentMessageViewHolder
            val sentHolder = holder as SentMessageViewHolder
            sentHolder.textViewSentMessage.text = message.content
        }
    }

    // Get total number of messages
    override fun getItemCount(): Int {
        return messages.size
    }

    // Determine the view type for the message (received or sent)
    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) SENT_MESSAGE else RECEIVED_MESSAGE
    }
}
