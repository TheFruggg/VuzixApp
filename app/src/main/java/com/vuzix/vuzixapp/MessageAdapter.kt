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

    private val RECEIVED_MESSAGE = 0
    private val SENT_MESSAGE = 1

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewReceivedMessage: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewSentMessage: TextView = itemView.findViewById(R.id.textViewSentMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == RECEIVED_MESSAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        if (holder.itemViewType == RECEIVED_MESSAGE) {
            val receivedHolder = holder as ReceivedMessageViewHolder
            receivedHolder.textViewReceivedMessage.text = message.content
        } else {
            val sentHolder = holder as SentMessageViewHolder
            sentHolder.textViewSentMessage.text = message.content
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) SENT_MESSAGE else RECEIVED_MESSAGE
    }
}
