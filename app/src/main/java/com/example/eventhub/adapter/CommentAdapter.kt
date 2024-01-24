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
    private val currentList: ArrayList<Comment>,
    private var database: FirebaseDatabase,
    private val context: Activity,
    private var event: Post? = null,
    private var thisUser: User? = null

) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

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


//
//            commentBox.setOnLongClickListener {
//                if (curComment.userId == thisUser?.userid && context != null) {
//                    showDeleteCommentDialog(curComment)
//                } else {
//                    Toast.makeText(context, "Can't delete other's comments.", Toast.LENGTH_SHORT).show()
//                }
//                true  // Return true to indicate that the long click event is consumed
//            }

        }
    }
//    private fun showDeleteCommentDialog(comment: Comment) {
//        val alertDialog = AlertDialog.Builder(context)
//        alertDialog.setTitle("Delete Comment")
//        alertDialog.setMessage("Are you sure you want to delete this comment?")
//
//        alertDialog.setPositiveButton("Delete") { dialog, _ ->
//            // Delete the comment from the database
//            deleteComment(comment.commentId)
//            dialog.dismiss()
//        }
//
//        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
//            dialog.dismiss()
//        }
//
//        alertDialog.create().show()
//    }
//
//
//
//    private fun deleteComment(commentId: String) {
//        val Post = event
//        val eventKey = Post!!.eventKey // Use the not-null assertion here
//
//        val commRef = database.getReference("Events/$eventKey/Comments")
//
//        // Remove the comment from the database using commentId
//        commRef.child(commentId).removeValue()
//            .addOnSuccessListener {
//                Toast.makeText(context, "Comment deleted successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show()
//            }
//    }
}
