package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConversationAdapter(private val conversations: List<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewContactName: TextView = itemView.findViewById(R.id.textViewContactName)
        val textViewLastMessage: TextView = itemView.findViewById(R.id.textViewLastMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val currentConversation = conversations[position]
        holder.textViewContactName.text = currentConversation.contactName
        holder.textViewLastMessage.text = currentConversation.lastMessage
    }

    override fun getItemCount() = conversations.size
}
