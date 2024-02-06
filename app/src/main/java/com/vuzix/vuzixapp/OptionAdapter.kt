package com.vuzix.vuzixapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class OptionAdapter(private val options: List<Option>) :
    RecyclerView.Adapter<OptionAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false) as Button
        return OptionViewHolder(button)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val option = options[position]
        holder.button.text = option.title
        holder.button.setOnClickListener { option.action() }
    }

    override fun getItemCount(): Int = options.size

    class OptionViewHolder(val button: Button) : RecyclerView.ViewHolder(button)
}
