package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.databinding.ItemCommentBinding
import com.example.eventhub.models.Comment

class CommentAdapter(
    private val currentList: ArrayList<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(
        val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentAdapter.CommentViewHolder =
        CommentViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: CommentAdapter.CommentViewHolder, position: Int) {
        val curComment = currentList[position]

        holder.binding.apply {
            itemComment.text = curComment.comment
            itemCommentNameTv.text = curComment.userName

            Glide.with(root.context)
                .load(curComment.userName)
                .into(itemCommentAvatarTv)
        }
    }
}

