package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.R
import com.example.eventhub.models.User

class FriendsAdapter (
    private val frieList: ArrayList<User>
    ) : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>(){


        class FriendsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            val friendimage : ImageView = itemView.findViewById(R.id.friends_user_image)
            val friendname : TextView = itemView.findViewById(R.id.friends_username)

            val card : CardView = itemView.findViewById(R.id.friends_card)

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false)

        return FriendsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return frieList.size
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friend = frieList[position]

        holder.apply {
            friendname.text = friend.name

            Glide.with(itemView)
                .load(friend.pfp)
                .into(friendimage)

            card.setOnClickListener{

            }


        }
    }


}