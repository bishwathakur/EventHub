package com.example.eventhub.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.PostDetailsActivity
import com.example.eventhub.R
import com.example.eventhub.databinding.ItemCommentBinding
import com.example.eventhub.databinding.ItemEventsBinding
import com.example.eventhub.models.Post
import com.example.eventhub.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class CommentAdapter(
    private val eveList: ArrayList<Post>,
    private val auth: FirebaseAuth,
    private val evedetRef : DatabaseReference,
    private val eveRef : DatabaseReference
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(
        val binding: ItemCommentBinding

    ) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentViewHolder =
        CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}