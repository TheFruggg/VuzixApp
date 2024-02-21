package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // List of messages
    private var messages: List<Message> = ArrayList()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views representing received and sent messages
        val textViewReceivedMessage: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
        val textViewSentMessage: TextView = itemView.findViewById(R.id.textViewSentMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflating the item_message layout for each message
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // Binding message data to the views
        val message = messages[position]
        if (message.isSent) {
            holder.textViewSentMessage.text = message.text
            holder.textViewSentMessage.visibility = View.VISIBLE
            holder.textViewReceivedMessage.visibility = View.GONE
        } else {
            holder.textViewReceivedMessage.text = message.text
            holder.textViewReceivedMessage.visibility = View.VISIBLE
            holder.textViewSentMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        // Return the total number of messages
        return messages.size
    }

    // Function to submit a new list of messages
    fun submitList(newList: List<Message>) {
        messages = newList
        notifyDataSetChanged()
    }
}
