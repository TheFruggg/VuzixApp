package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class OptionAdapter(private val options: List<Option>) :
    RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    // Function to create a new ViewHolder for each option item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        // Inflating the item_option layout for each option
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false) as Button
        return OptionViewHolder(button)
    }



    // Function to bind data to each ViewHolder
    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        // Set the title of the button to the option title
        holder.button.text = option.title
        // Set click listener to execute the action associated with the option
        holder.button.setOnClickListener { option.action() }
    }

    // Function to return the total number of options
    override fun getItemCount(): Int = options.size

    // ViewHolder class to hold the button view
    class OptionViewHolder(val button: Button) : RecyclerView.ViewHolder(button)
}
