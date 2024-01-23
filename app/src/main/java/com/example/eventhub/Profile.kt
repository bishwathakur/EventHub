package com.example.eventhub

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.eventhub.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.compose.material3.TopAppBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.eventhub.adapter.PostAdapter
import com.example.eventhub.adapter.ProfilePostAdapter
import com.example.eventhub.models.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class Profile : Fragment() {



    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference

    //Texts
    private lateinit var insusername: TextView
    private lateinit var insuserid: TextView
    private lateinit var insuserplace: TextView
    private lateinit var insuseremail: TextView
    private lateinit var insuserphone: TextView
    private lateinit var insuserpfp: CircleImageView

    private lateinit var eveRecyclerView: RecyclerView
    private lateinit var loadingcircle: ProgressBar


    private lateinit var eveList: ArrayList<Post>
    private lateinit var mAdapter: ProfilePostAdapter


    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    private lateinit var dialog: Dialog

    private lateinit var more : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        eveRecyclerView = view.findViewById(R.id.profile_rec)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProfile)

        eveRecyclerView.layoutManager = LinearLayoutManager(context)
        eveRecyclerView.setHasFixedSize(true)
        loadingcircle = view.findViewById(R.id.profile_progress_bar)

        eveList = arrayListOf<Post>()

        more = view.findViewById(R.id.more)

        // Initialize databaseReference and firebaseAuth
        dataBaseReference = FirebaseDatabase.getInstance().getReference("EventDetails")
        auth = FirebaseAuth.getInstance()

        mAdapter = ProfilePostAdapter(eveList)
        eveRecyclerView.adapter = mAdapter

        getEvents()
        swipeRefreshLayout.setOnRefreshListener {
            // Perform data refresh operations here

            // For example, you might want to update your RecyclerView data
            reloadAdapter()

            // Once done, call isRefreshing = false to stop the loading animation
            swipeRefreshLayout.isRefreshing = false
        }


        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        insusername = view.findViewById(R.id.username)
        insuserid = view.findViewById(R.id.userid)
        insuserplace = view.findViewById(R.id.userplace)
        insuseremail = view.findViewById(R.id.useremail)
        insuserphone = view.findViewById(R.id.userphone)
        insuserpfp = view.findViewById(R.id.userpfpdisplay)

        // Inittialize the DatabaseReference
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Insnapshotialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Retrieve data from the Realtime Database
        fetchDataFromDatabase()

    }
    private fun getEvents() {

        eveRecyclerView.visibility = View.GONE
        loadingcircle.visibility = View.VISIBLE

        dataBaseReference = FirebaseDatabase.getInstance().getReference("Events")

        dataBaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eveList.clear()
                if (snapshot.exists()) {
                    for (eveSnap in snapshot.children) {
                        val eveData = eveSnap.getValue(Post::class.java)
                        eveList.add(eveData!!)
                    }
                    // Notify the adapter that the dataset has changed
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

    private fun reloadAdapter() {
        getEvents()
        // Notify the adapter that the dataset has changed
        mAdapter.notifyDataSetChanged()
    }



    private fun fetchDataFromDatabase() {
        // Check if the user is authenticated
        val uid = auth.currentUser!!.uid

        dataBaseReference.child(uid).get().addOnSuccessListener {snapshot ->

            if (snapshot!=null) {


                val name = snapshot.child("name").value.toString()
                val userid = snapshot.child("userid").value.toString()
                val userplace = snapshot.child("userplace").value.toString()
                val email = snapshot.child("email").value.toString()
                val userphone = snapshot.child("userphone").value.toString()
                val userpfp = snapshot.child("pfp").value.toString()

                insusername.text = name
                insuserid.text = userid
                insuserplace.text = userplace
                insuseremail.text = email
                insuserphone.text = userphone


                Glide.with(requireContext())
                    .load(userpfp)
                    .into(insuserpfp)
            }

            more.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), more)
                popupMenu.menuInflater.inflate(R.menu.menu_profilelogout, popupMenu.menu)

                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.logout -> {
                            auth.signOut()
                            startActivity(Intent(requireActivity(), SignInActivity::class.java))
                            requireActivity().finish()
                        }
                    }
                    true
                })
                popupMenu.show()
            }
        }
    }}
