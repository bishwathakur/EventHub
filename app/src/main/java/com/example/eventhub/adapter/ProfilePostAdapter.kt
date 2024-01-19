package com.example.eventhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.databinding.ItemEventsBinding
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class ProfilePostAdapter(
    private val eveList: ArrayList<Post>


) : RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder>(){

        inner class PostViewHolder(val binding: ItemEventsBinding):
            RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilePostAdapter.PostViewHolder =
        PostViewHolder(
            ItemEventsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int {
        return eveList.size
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentEvent = eveList[position]

        val auth = FirebaseAuth.getInstance()

        holder.binding.apply {
            if (currentEvent.userEmail.toString() == currentEvent.userEmail){

                postEventName.text = currentEvent.eventname
                postEventVenue.text = currentEvent.eventvenue
                postEventDate.text = currentEvent.eventdate
                postEventByuser.text = currentEvent.userId

                // Initialize Glide and load the image into the ImageView
                Glide.with(root.context)
                    .load(currentEvent.userImage)
                    .into(postUserPicture)

                Glide.with(root.context)
                    .load(currentEvent.eventpicUrl)
                    .into(postImage)

                postLikesTV.text = "${currentEvent.postLikes} likes"
                postCommentTV.text = "${currentEvent.postComments} comments"
            }
        }
    }
}