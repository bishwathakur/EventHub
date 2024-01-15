package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eventhub.adapter.PostAdapter
import com.example.eventhub.models.Post
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : Fragment() {

    private lateinit var eveRecyclerView: RecyclerView
    private lateinit var loadingcircle: ProgressBar
    private lateinit var addBtn: FloatingActionButton
    private lateinit var eveList: ArrayList<Post>
    private lateinit var dbRef: DatabaseReference
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        eveRecyclerView = view.findViewById(R.id.home_rec)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            // Perform data refresh operations here
            // Once done, call isRefreshing = false to stop the loading animation
            swipeRefreshLayout.isRefreshing = false
        }


        eveRecyclerView.layoutManager = LinearLayoutManager(context)
        eveRecyclerView.setHasFixedSize(true)
        loadingcircle = view.findViewById(R.id.home_progress_bar)

        eveList = arrayListOf<Post>()

        // Initialize addBtn and set its click listener
        addBtn = view.findViewById(R.id.home_addButton)
        addBtn.setOnClickListener {
            // Open AddEventActivity when the button is clicked
            val intent = Intent(activity, AddEventActivity::class.java)
            startActivity(intent)
        }

        getEvents()

        return view
    }

    private fun getEvents() {
        eveRecyclerView.visibility = View.GONE
        loadingcircle.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Events")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eveList.clear()
                if (snapshot.exists()) {
                    for (eveSnap in snapshot.children) {
                        val eveData = eveSnap.getValue(Post::class.java)
                        eveList.add(eveData!!)
                    }
                    val mAdapter = PostAdapter(eveList)
                    eveRecyclerView.adapter = mAdapter
                }

                eveRecyclerView.visibility = View.VISIBLE
                loadingcircle.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                loadingcircle.visibility = View.GONE
            }
        })
    }
}
