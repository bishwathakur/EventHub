package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.R
import com.example.eventhub.models.Post

class PostAdapter(private val eveList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_events, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEvent = eveList[position]

        holder.tveventname.text = currentEvent.eventname
        holder.tveventvenue.text = currentEvent.eventvenue
        holder.tveventndate.text = currentEvent.eventdate
        holder.tveventbyuser.text = currentEvent.userId


//        holder.tveventLi.text = currentEvent.eventname
//        holder.tveventname.text = currentEvent.eventname
    }



    override fun getItemCount(): Int {
        return eveList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tveventname: TextView = itemView.findViewById(R.id.post_event_name)
        val tveventvenue: TextView = itemView.findViewById(R.id.post_event_venue)
        val tveventndate: TextView = itemView.findViewById(R.id.post_event_date)
        val tveventbyuser: TextView = itemView.findViewById(R.id.post_event_byuser)

        // Other TextViews...
//        val tveventLikescount : TextView = itemView.findViewById(R.id.post_LikesTV)
//        val tveventCommentscount : TextView = itemView.findViewById(R.id.post_CommentTV)


    }

}