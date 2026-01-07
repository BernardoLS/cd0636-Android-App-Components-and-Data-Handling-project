package com.udacity.project.spire.ui.countries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Simple adapter for displaying a list of strings in a RecyclerView.
 * Used for displaying country names.
 *
 * TODO #45: Implement SimpleTextAdapter
 *
 * This adapter:
 * 1. Extends RecyclerView.Adapter directly (simplest form)
 * 2. Displays simple text items (country names)
 * 3. Uses Android's built-in simple_list_item_1 layout
 *
 * KEY CONCEPTS:
 * - RecyclerView.Adapter: Base class for all adapters
 * - getItemCount(): Required to tell RecyclerView how many items exist
 * - notifyDataSetChanged(): Tells RecyclerView to redraw all items
 */
class SimpleTextAdapter(
    private var items: List<String>
) : RecyclerView.Adapter<SimpleTextAdapter.TextViewHolder>() {

    /**
     * TODO #45a: Implement onCreateViewHolder()
     *
     * HINTS:
     * - Inflate android.R.layout.simple_list_item_1 using LayoutInflater
     * - Cast result to TextView
     * - Return TextViewHolder(textView)
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        TODO("Implement onCreateViewHolder() - see TODO comment above")
    }

    /**
     * TODO #45b: Implement onBindViewHolder()
     *
     * HINT: Call holder.bind(items[position])
     */
    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        TODO("Implement onBindViewHolder() - see TODO comment above")
    }

    /**
     * TODO #45c: Implement getItemCount()
     *
     * HINT: Return items.size
     */
    override fun getItemCount(): Int {
        TODO("Implement getItemCount() - see TODO comment above")
    }

    /**
     * Update adapter data.
     * Called by Fragment when new country list is received.
     */
    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged() // Tell RecyclerView to redraw all items
    }

    /**
     * ViewHolder for text items.
     *
     * TODO #45d: Implement bind() method
     */
    class TextViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        /**
         * TODO #45d: Implement bind()
         *
         * HINT: Set textView.text = text
         */
        fun bind(text: String) {
            TODO("Implement bind() - see TODO comment above")
        }
    }
}
