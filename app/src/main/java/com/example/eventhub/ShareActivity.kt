package com.example.eventhub

import Home
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.adapter.FriendsAdapter
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShareActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference

    private lateinit var frieRecyclerView: RecyclerView
    private lateinit var frieList: ArrayList<User>

    private lateinit var loader: ProgressBar

    private lateinit var mAdapter: FriendsAdapter

    private lateinit var exittohome : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        frieRecyclerView = findViewById(R.id.friends_rec) // Initialize frieRecyclerView here

        frieRecyclerView.layoutManager = LinearLayoutManager(this)
        frieRecyclerView.setHasFixedSize(true)

        loader = findViewById(R.id.friends_progress_bar)
        exittohome = findViewById(R.id.menuIcon)

        frieList = arrayListOf<User>()

        auth = FirebaseAuth.getInstance()
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        mAdapter = FriendsAdapter(frieList)
        frieRecyclerView.adapter = mAdapter


        mAdapter.onItemClick = {
            val intent = Intent(this, ChattingActivity::class.java)
            intent.putExtra("user", it)
            startActivity(intent)
            finish()
        }

        getFriendsList()

        exittohome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getFriendsList(){

        frieRecyclerView.visibility = View.GONE
        loader.visibility = View.VISIBLE

        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        dataBaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                frieList.clear()
                if (snapshot.exists()) {
                    for (frieSnap in snapshot.children) {
                        val frieData = frieSnap.getValue(User::class.java)
                        frieList.add(frieData!!)
                    }
                    mAdapter.notifyDataSetChanged()
                }
                frieRecyclerView.visibility = View.VISIBLE
                loader.visibility = View.GONE
            }






            override fun onCancelled(error: DatabaseError) {
                loader.visibility = View.GONE
            }


        })
    }






}