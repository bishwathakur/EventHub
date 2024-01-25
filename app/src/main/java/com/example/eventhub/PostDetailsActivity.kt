package com.example.eventhub

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.adapter.CommentAdapter
import com.example.eventhub.models.Comment
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var commRecyclerView: RecyclerView
    private lateinit var loadingcircle: ProgressBar
    private lateinit var commList: ArrayList<Comment>
    private lateinit var commRef: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var addComment: ImageView
    private lateinit var thisUser : FirebaseUser

    private lateinit var comment: TextView
    private var event: Post? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postdetails)


        val post = intent.getParcelableExtra<Post?>("post")
        event = post
        if (post != null){
            val nameevent : TextView = findViewById(R.id.postdetails_event_name)
            val dateevent : TextView = findViewById(R.id.postdetails_event_date)
            val venueevent : TextView = findViewById(R.id.postdetails_event_venue)
            val byuserevent : TextView = findViewById(R.id.postdetails_event_byuser)
            val imageevent : ImageView = findViewById(R.id.postdetails_Image)
            val eventuserimage : ImageView = findViewById(R.id.postdetails_userPicture)
            val eventlikesTV : TextView = findViewById(R.id.postdetails_LikesTV)
            val eventcommentsTV : TextView = findViewById(R.id.postdetails_CommentTV)



            nameevent.text = post.eventname
            dateevent.text = post.eventdate
            venueevent.text = post.eventvenue
            byuserevent.text = post.userId
            eventlikesTV.text = "${post.postLikes} likes"
            eventcommentsTV.text = "${post.postComments} comments"


            Glide.with(this)
                .load(post.eventpicUrl)
                .into(imageevent)
            Glide.with(this)
                .load(post.userImage)
                .into(eventuserimage)

        }


        commRecyclerView = findViewById(R.id.det_rec_comments)
        addComment = findViewById(R.id.det_btn_comment)
        comment = findViewById(R.id.det_commentEt)


        commRecyclerView.layoutManager = LinearLayoutManager(this)
        commRecyclerView.setHasFixedSize(true)
        loadingcircle = findViewById(R.id.det_ProgressBar_comments)

        commList = arrayListOf<Comment>()

        firebaseAuth = FirebaseAuth.getInstance()

        val currentUser: FirebaseUser? = firebaseAuth.currentUser

        currentUser?.let {
            thisUser = it
        }
        commRef = FirebaseDatabase.getInstance().getReference("Events")

        commentAdapter = CommentAdapter(commList)

        commentAdapter.onItemLongClick = { comment ->
            // Handle long click on the comment
            // Check if the comment's username matches the current user's name
            if (comment.uid == thisUser.uid) {
                // The comment was made by the current user
                showDeleteCommentDialog(comment)
            }else{
                Toast.makeText(this, "Cant delete other's comments", Toast.LENGTH_SHORT).show()
            }
        }


        commRecyclerView.adapter = commentAdapter





        getComments()


        addComment.setOnClickListener {

            saveCommentData()
        }

        //Implement a back feature to go a stackk behind not exit the app...
    }

    private fun getComments() {
        commRecyclerView.visibility = View.GONE
        loadingcircle.visibility = View.VISIBLE

        val Post = event ?: return

        commRef = FirebaseDatabase.getInstance().getReference("Events/${Post.eventKey}/Comments")

        commRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commList.clear()
                if (snapshot.exists()) {
                    for (commSnap in snapshot.children) {
                        val commData = commSnap.getValue(Comment::class.java)
                        commList.add(commData!!)
                    }
                    // Notify the adapter that the dataset has changed
                    commentAdapter.notifyDataSetChanged()
                }

                commRecyclerView.visibility = View.VISIBLE
                loadingcircle.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                loadingcircle.visibility = View.GONE
            }
        })
    }

    private fun saveCommentData() {
        val commentLine = comment.text.toString()

        if (commentLine.isEmpty()) {
            Toast.makeText(this, "Enter your comment", Toast.LENGTH_SHORT).show()
            return
        }

        uploadCommentToDatabase(commentLine)

        // Clear the EditText after uploading the comment
        comment.setText("")
        hideKeyboard(this)
    }

    private fun uploadCommentToDatabase(commentline: String) {

        val Post = event ?: return

        val commRef = FirebaseDatabase.getInstance().getReference("Events/${Post?.eventKey}/Comments")

        val commentId = commRef.push().key

        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(User::class.java)

                    userData?.let { user ->
                        val newComment = Comment(
                            commentId ?: "",
                            commentline,
                            user.userid.toString(),
                            user.pfp.toString(),
                            user.name.toString(),
                            firebaseAuth.uid.toString()
                        )

                        commRef.child(commentId ?: "").setValue(newComment)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@PostDetailsActivity,
                                    "Comment Added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()


                            }.addOnFailureListener {
                                Toast.makeText(
                                    this@PostDetailsActivity,
                                    "Failed to add comment",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    private fun showDeleteCommentDialog(comment: Comment) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Delete Comment")
        alertDialog.setMessage("Are you sure you want to delete this comment?")

        alertDialog.setPositiveButton("Delete") { dialog, _ ->
            // Delete the comment from the database
            deleteComment(comment.commentId)
            dialog.dismiss()
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog.create().show()
    }
    private fun deleteComment(commentId: String) {
        val Post = event
        val eventKey = Post!!.eventKey // Use the not-null assertion here

        val commRef = FirebaseDatabase.getInstance().getReference("Events/$eventKey/Comments")

        // Remove the comment from the database using commentId
        commRef.child(commentId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Comment deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete comment", Toast.LENGTH_SHORT).show()
            }
    }
    private fun hideKeyboard(activity: Activity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}