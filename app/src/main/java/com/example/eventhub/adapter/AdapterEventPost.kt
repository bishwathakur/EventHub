package com.example.eventhub.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.R
import com.example.eventhub.models.Event

class AdapterEventPost(private var events: MutableList<Event>) :
    RecyclerView.Adapter<AdapterEventPost.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: TextView = itemView.findViewById(R.id.ins_event_photo)
        val itemName: TextView = itemView.findViewById(R.id.ins_event_name)
        val itemDate: TextView = itemView.findViewById(R.id.ins_event_date)
        val itemVenue: TextView = itemView.findViewById(R.id.ins_event_venue)
        val itemUser: TextView = itemView.findViewById(R.id.ins_event_byuser)

        init {
            itemView.setOnClickListener {
                // Handle click event if needed
                // Example: val clickedEvent = events[adapterPosition]
//
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_events, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEvent = events[position]

        // Update UI with event details
        holder.itemImage.text = currentEvent.eventimage // Update with the correct property
        holder.itemName.text = currentEvent.eventname
        holder.itemDate.text = currentEvent.eventdate
        holder.itemVenue.text = currentEvent.eventvenue
        holder.itemUser.text = currentEvent.eventbyuser
    }

    // Add a method to update the data dynamically
    fun addData(newEvent: Event) {
        events.add(newEvent)
        notifyItemInserted(events.size - 1)
    }
}
