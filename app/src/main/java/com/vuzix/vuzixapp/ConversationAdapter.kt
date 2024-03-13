package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ConversationAdapter(
    private val conversations: List<Conversation>,
    private val onItemClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantsTextView: TextView = itemView.findViewById(R.id.participantsTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        val otherUserId = conversation.participants.firstOrNull { it != FirebaseAuth.getInstance().currentUser?.uid }
        otherUserId?.let { userId ->
            getUserFirstName(userId) { firstName ->
                holder.participantsTextView.text = firstName
            }
        }
        holder.itemView.setOnClickListener { onItemClick(conversation) }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    private fun getUserFirstName(userId: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val firstName = documentSnapshot.getString("firstName") ?: ""
                callback(firstName)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                callback("Unknown")
            }
    }
}
