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
import android.widget.Toast
import androidx.compose.material3.TopAppBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class Profile : Fragment() {


    private lateinit var binding : FragmentProfileBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference

    //Texts
    private lateinit var insusername: TextView
    private lateinit var insuserid: TextView
    private lateinit var insuserplace: TextView
    private lateinit var insuseremail: TextView
    private lateinit var insuserphone: TextView

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        insusername = view.findViewById(R.id.username)
        insuserid = view.findViewById(R.id.userid)
        insuserplace = view.findViewById(R.id.userplace)
        insuseremail = view.findViewById(R.id.useremail)
        insuserphone = view.findViewById(R.id.userphone)

        // Insnapshotialize the DatabaseReference
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Insnapshotialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutProfile)

        swipeRefreshLayout.setOnRefreshListener {
            // Perform data refresh operations here
            // Once done, call isRefreshing = false to stop the loading animation
            swipeRefreshLayout.isRefreshing = false
        }

        // Retrieve data from the Realtime Database
        fetchDataFromDatabase()

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
                val userpfp = snapshot.child("userpfp").value.toString()

                insusername.text = name
                insuserid.text = userid
                insuserplace.text = userplace
                insuseremail.text = email
                insuserphone.text = userphone

                val requestOptions = RequestOptions()
                    .placeholder(R.drawable.add_profilepic) // Your placeholder image resource
                    .error(R.drawable.ic_adddp) // Your error image resource

                if (userpfp != null) {
                    Glide.with(requireContext())
                        .load(userpfp)
                        .apply(requestOptions)
                        .transition(DrawableTransitionOptions.withCrossFade()) // Optional crossfade animation
                        .into(binding.userpfpdisplay)
                } else {
                    // Handle the case when userpfp is null (set a default image, show an error message, etc.)
                    Glide.with(requireContext())
                        .load(R.drawable.default_user) // Your default image resource
                        .apply(requestOptions)
                        .transition(DrawableTransitionOptions.withCrossFade()) // Optional crossfade animation
                        .into(binding.userpfpdisplay)
                }
            }

        binding.more.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.more)
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
