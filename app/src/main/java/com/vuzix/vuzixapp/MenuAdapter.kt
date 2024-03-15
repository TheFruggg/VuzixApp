package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private val options: List<String>, private val itemClickListener: (Int) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val optionTextView: TextView = itemView.findViewById(R.id.optionTextView)
    }

    // Create new ViewHolder instance
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate layout for menu item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return ViewHolder(view)
    }

    // Bind data to ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Set option text in TextView
        holder.optionTextView.text = options[position]
        // Set click listener on item view
        holder.itemView.setOnClickListener {
            // Invoke itemClickListener with position
            itemClickListener.invoke(position)
        }
    }

    // Get total number of menu items
    override fun getItemCount(): Int {
        return options.size
    }
}
