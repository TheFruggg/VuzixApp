package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConversationAdapter(
    private val conversations: List<Conversation>, // List of conversations to display
    private val onItemClick: (Conversation) -> Unit // Callback for item click
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    // ViewHolder for conversation items
    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantsTextView: TextView = itemView.findViewById(R.id.participantsTextView) // TextView for participant names
    }

    // Create new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        // Inflate layout for conversation item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position] // Get conversation at current position
        val otherUserId = conversation.participants.firstOrNull { it != FirebaseAuth.getInstance().currentUser?.uid } // Get ID of the other participant
        otherUserId?.let { userId ->
            // Get first name of the other participant
            getUserFirstName(userId) { firstName ->
                holder.participantsTextView.text = firstName // Set first name in TextView
            }
        }
        holder.itemView.setOnClickListener { onItemClick(conversation) } // Set click listener on item view
    }

    // Get total number of conversations
    override fun getItemCount(): Int {
        return conversations.size
    }

    // Fetch first name of a user from Firestore
    private fun getUserFirstName(userId: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance() // Get instance of Firestore
        // Query Firestore to get user's first name
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val firstName = documentSnapshot.getString("firstName") ?: "" // Get first name from document
                callback(firstName) // Invoke callback with first name
            }
            .addOnFailureListener { exception ->
                // Handle failure
                callback("Unknown") // Set first name as "Unknown" in case of failure
            }
    }
}
