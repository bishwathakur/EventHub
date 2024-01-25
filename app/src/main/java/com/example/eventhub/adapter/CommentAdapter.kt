package com.example.eventhub.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.databinding.ItemCommentBinding
import com.example.eventhub.models.Comment
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.google.firebase.database.FirebaseDatabase

class CommentAdapter(
    private val currentList: ArrayList<Comment>

) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    var onItemLongClick: ((Comment) -> Unit)? = null

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder =
        CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = currentList.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val curComment = currentList[position]

        holder.binding.apply {
            itemComment.text = curComment.comment
            itemCommentNameTv.text = curComment.userName

            Glide.with(root.context)
                .load(curComment.userImage)
                .into(itemCommentAvatarTv)



            commentBox.setOnLongClickListener {
                onItemLongClick?.invoke(curComment)
                true
            }

        }
    }
//


}
