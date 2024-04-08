package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConversationAdapter(
    private val conversations: List<Conversation>,
    private val onItemClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    // Position of the currently selected item
    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        // Inflate the layout for each conversation item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        val otherUserId = conversation.participants.firstOrNull { it != FirebaseAuth.getInstance().currentUser?.uid }

        // Setting the background based on selection
        if (position == selectedPosition) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rounded_background_selected)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.rounded_background)
        }

        // Fetch user's first name and display it
        otherUserId?.let { userId ->
            getUserFirstName(userId) { firstName ->
                holder.participantsTextView.text = firstName
            }
        }

        // Handle click event on conversation item
        holder.itemView.setOnClickListener {
            onItemClick(conversation)
            selectItem(position)
        }
    }

    override fun getItemCount() = conversations.size

    // Method to update the selected item position
    fun selectItem(position: Int) {
        notifyItemChanged(selectedPosition)
        selectedPosition = position
        notifyItemChanged(selectedPosition)
    }

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // View holder for conversation item
        val participantsTextView: TextView = itemView.findViewById(R.id.participantsTextView)
    }

    // Method to fetch user's first name from Firestore
    private fun getUserFirstName(userId: String, callback: (String) -> Unit) {
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { documentSnapshot ->
                val firstName = documentSnapshot.getString("firstName") ?: "Unknown"
                callback(firstName)
            }
            .addOnFailureListener {
                callback("Unknown")
            }
    }
}
