package com.example.eventhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.adapter.CommentAdapter
import com.example.eventhub.models.Comment
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
    private lateinit var addComment: Button

    private lateinit var comment: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postdetails)

        commRecyclerView = findViewById(R.id.det_rec_comments)
        addComment = findViewById(R.id.det_btn_comment)
        comment = findViewById(R.id.det_commentEt)

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
    }

    private fun getComments() {
        commRecyclerView.visibility = View.GONE
        loadingcircle.visibility = View.VISIBLE

        commRef = FirebaseDatabase.getInstance().getReference("Events")

        commRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commList.clear()
                if (snapshot.exists()) {
                    for (eveSnap in snapshot.children) {
                        val commData = eveSnap.getValue(Comment::class.java)
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

        if (commentline.isEmpty()) comment.error = "Enter your Comment!!"

        uploadCommenttoDatabase(commentline)
    }

    private fun uploadCommenttoDatabase(commentline: String) {

        val commRef = FirebaseDatabase.getInstance().getReference("Events")

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

                        commRef.child("Commnents").child(commentId ?: "").setValue(newComment)
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
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //mehhhhhhhhhh
                }
            })
        }
    }
}