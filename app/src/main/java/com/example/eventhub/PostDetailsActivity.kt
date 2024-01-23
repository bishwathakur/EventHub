package com.example.eventhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventhub.adapter.CommentAdapter
import com.example.eventhub.models.Comment
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PostDetailsActivity : AppCompatActivity() {

    private lateinit var commRecyclerView: RecyclerView
    private lateinit var loadingcircle: ProgressBar
    private lateinit var commList: ArrayList<Comment>
    private lateinit var commRef: DatabaseReference
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var addComment: ImageView
    private lateinit var auth: FirebaseAuth

    private lateinit var comment: TextView
    private var event: Post? = null
    private var thisUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postdetails)


        val event = intent.getParcelableExtra<Post?>("post")
        if (event != null){
            val nameevent : TextView = findViewById(R.id.postdetails_event_name)
            val dateevent : TextView = findViewById(R.id.postdetails_event_date)
            val venueevent : TextView = findViewById(R.id.postdetails_event_venue)
            val byuserevent : TextView = findViewById(R.id.postdetails_event_byuser)
            val imageevent : ImageView = findViewById(R.id.postdetails_Image)
            val eventuserimage : ImageView = findViewById(R.id.postdetails_userPicture)
            val eventlikesTV : TextView = findViewById(R.id.postdetails_LikesTV)
            val eventcommentsTV : TextView = findViewById(R.id.postdetails_CommentTV)




            nameevent.text = event.eventname
            dateevent.text = event.eventdate
            venueevent.text = event.eventvenue
            byuserevent.text = event.userId
            eventlikesTV.text = "${event.postLikes} likes"
            eventcommentsTV.text = "${event.postComments} comments"


            Glide.with(this)
                .load(event.eventpicUrl)
                .into(imageevent)
            Glide.with(this)
                .load(event.userImage)
                .into(eventuserimage)

        }



        commRecyclerView = findViewById(R.id.det_rec_comments)
        addComment = findViewById(R.id.det_btn_comment)
        comment = findViewById(R.id.det_commentEt)
        val commentingpfp : ImageView = findViewById(R.id.comments_Userpfp)


        var pfpurl = thisUser?.pfp

        Glide.with(this)
            .load(pfpurl)
            .into(commentingpfp)

        commRecyclerView.layoutManager = LinearLayoutManager(this)
        commRecyclerView.setHasFixedSize(true)
        loadingcircle = findViewById(R.id.det_ProgressBar_comments)

        commList = arrayListOf<Comment>()

        // Initialize databaseReference and firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        commRef = FirebaseDatabase.getInstance().getReference("Events")

        commentAdapter = CommentAdapter(commList)
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

        commRef = FirebaseDatabase.getInstance().getReference("Events/${Post?.eventKey}/Comments")

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


        val commentline = comment.text.toString()

        uploadCommenttoDatabase(commentline)
    }

    private fun uploadCommenttoDatabase(commentline: String) {

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
                            user.name.toString()
                        )

                        commRef.child(commentId ?: "").setValue(newComment)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@PostDetailsActivity,
                                    "Comment Added Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()


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
}