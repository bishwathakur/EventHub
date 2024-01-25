package com.example.eventhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.R
import com.example.eventhub.models.Chat
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatAdapter(private val context: Context, val chatList: ArrayList<Chat>, var user: User? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MESSAGE_TYPE_LEFT) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.sent_msg_layout, parent, false)
            return SentViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(context).inflate(R.layout.received_msg_layout, parent, false)
            return ReceiveViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = chatList[position]

        if (holder.javaClass == SentViewHolder::class.java){
            val viewHolder = holder as SentViewHolder
            viewHolder.sentmessage.text = currentMessage.message
        }else{
            val viewHolder =  holder as ReceiveViewHolder
            viewHolder.recmessage.text = currentMessage.message
        }

    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentmessage = itemView.findViewById<TextView>(R.id.sent_msg_text)
    }
    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recmessage = itemView.findViewById<TextView>(R.id.rec_msg_text)
    }

    override fun getItemViewType(position: Int): Int {

        val ID = user?.userid

        return if (chatList[position].senderID == ID) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }

    }
}