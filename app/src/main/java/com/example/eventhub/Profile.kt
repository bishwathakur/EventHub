package com.example.eventhub

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
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    private lateinit var image: CircleImageView // Assuming you are using a CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize your views here
        val tvusername: TextView = binding.username
        val tvuserid: TextView = binding.userid
        val tvuserplace: TextView = binding.userplace
        val tvuseremail: TextView = binding.useremail
        val tvuserphone: TextView = binding.userphone
        image = binding.userpfp // Assuming you have a CircleImageView in your layout

        // Initialize auth
        auth = FirebaseAuth.getInstance()

        // Set up your views or perform other tasks
        // ...
        val uid = auth.currentUser!!.uid

        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        dataBaseReference.child(uid).get().addOnSuccessListener {

            val name = it.child("name").value.toString()
            val userid = it.child("userid").value.toString()
            val userplace = it.child("userplace").value.toString()
            val email = it.child("email").value.toString()
            val userphone = it.child("userphone").value.toString()
            val userpfp = it.child("userpfp").value.toString()

            tvusername.text = name
            tvuserid.text = userid
            tvuserplace.text = userplace
            tvuseremail.text = email
            tvuserphone.text = userphone

            // Load profile picture using Glide
            Glide.with(requireContext())
                .load(userpfp)
                .into(image)

        }.addOnFailureListener {
            Snackbar.make(view, it.toString(), Snackbar.LENGTH_SHORT).show()
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
}
