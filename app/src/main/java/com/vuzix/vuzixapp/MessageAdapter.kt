package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Message> = ArrayList()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewReceivedMessage: TextView = itemView.findViewById(R.id.textViewReceivedMessage)
        val textViewSentMessage: TextView = itemView.findViewById(R.id.textViewSentMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
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
        return messages.size
    }

    fun submitList(newList: List<Message>) {
        messages = newList
        notifyDataSetChanged()
    }
}
