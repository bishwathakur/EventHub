package com.example.eventhub

import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.eventhub.adapter.ProfilePostAdapter
import com.example.eventhub.databinding.FragmentProfileBinding
import com.example.eventhub.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    private lateinit var mAdapter: ProfilePostAdapter
    private lateinit var eveRecyclerView: RecyclerView
    private lateinit var loadingcircle: ProgressBar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var eveList: ArrayList<Post>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        eveRecyclerView = binding.profileRec
        eveRecyclerView.layoutManager = LinearLayoutManager(context)
        eveRecyclerView.setHasFixedSize(true)

        loadingcircle = binding.profileProgressBar
        eveList = arrayListOf<Post>()

        // Initialize FirebaseAuth and DatabaseReference
        auth = FirebaseAuth.getInstance()
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Events")

        mAdapter = ProfilePostAdapter(eveList)
        eveRecyclerView.adapter = mAdapter

        swipeRefreshLayout = binding.swipeRefreshLayoutProfile

        swipeRefreshLayout.setOnRefreshListener {
            // Perform data refresh operations here
            getEvents()

            // Once done, call isRefreshing = false to stop the loading animation
            swipeRefreshLayout.isRefreshing = false
        }

        fetchDataFromDatabase()

        return binding.root
    }

    private fun fetchDataFromDatabase() {
        val uid = auth.currentUser!!.uid

        dataBaseReference.child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot != null) {
                val name = snapshot.child("name").value.toString()
                val userid = snapshot.child("userid").value.toString()
                val userplace = snapshot.child("userplace").value.toString()
                val email = snapshot.child("email").value.toString()
                val userphone = snapshot.child("userphone").value.toString()

                binding.username.text = name
                binding.userid.text = userid
                binding.userplace.text = userplace
                binding.useremail.text = email
                binding.userphone.text = userphone

                getEvents()
            }
        }
    }

    private fun getEvents() {
        eveRecyclerView.visibility = View.GONE
        loadingcircle.visibility = View.VISIBLE

        val uid = auth.currentUser!!.uid

        dataBaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eveList.clear()
                if (snapshot.exists()) {
                    for (eveSnap in snapshot.children) {
                        val eveData = eveSnap.getValue(Post::class.java)
                        eveList.add(eveData!!)
                    }
                    mAdapter.notifyDataSetChanged()
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
