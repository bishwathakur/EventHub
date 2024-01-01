package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.R
import com.example.eventhub.models.Event
import com.google.firebase.database.core.Context

class AdapterEventPost(val c:Context,val eventList:ArrayList<Event>) : RecyclerView.Adapter<AdapterEventPost.UserViewHolder>()
{
    inner class UserViewHolder(val v: View) :RecyclerView.ViewHolder(v) {
//        val eventimage = v.findViewById<TextView>(R.id.ins_event_photo)
        val eventname = v.findViewById<TextView>(R.id.ins_event_name)
        val eventdate = v.findViewById<TextView>(R.id.ins_event_date)
        val eventvenue = v.findViewById<TextView>(R.id.ins_event_venue)
        val eventbyuser = v.findViewById<TextView>(R.id.ins_event_byuser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterEventPost.UserViewHolder {
        val  inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_events,parent,false)
        return UserViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdapterEventPost.UserViewHolder, position: Int) {
        val newList = eventList[position]
         holder.eventname.text = newList.eventname
         holder.eventdate.text = newList.eventdate
         holder.eventvenue.text = newList.eventvenue
         holder.eventbyuser.text = newList.eventbyuser



    }

    override fun getItemCount(): Int {
        return eventList.size
    }
}