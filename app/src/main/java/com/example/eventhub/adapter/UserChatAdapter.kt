package com.example.eventhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.R
import com.example.eventhub.models.User
import de.hdodenhof.circleimageview.CircleImageView

class UserChatAdapter(
    private val context: Context,
    private val userList: List<User>,
    private val listener: OnItemClickListener // Add listener parameter
) : RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>() {

    // Interface for click events
    interface OnItemClickListener {
        fun onItemClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chats, parent, false)
        return UserChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        val user = userList[position]
        holder.username.text = user.name

        // Load user profile image
        Glide.with(context).load(user.pfp).into(holder.userImage)

        // Set the click listener on the item view
        holder.itemView.setOnClickListener {
            listener.onItemClick(user) // Call the click listener
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.chat_username)
        val userImage: CircleImageView = itemView.findViewById(R.id.chat_user_image)
    }
}
