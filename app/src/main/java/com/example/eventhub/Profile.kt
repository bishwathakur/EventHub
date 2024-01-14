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
import com.bumptech.glide.Glide
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

        // Initialize the DatabaseReference
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Retrieve data from the Realtime Database
        fetchDataFromDatabase()

    }


    private fun fetchDataFromDatabase() {
        // Check if the user is authenticated
        val user = auth.currentUser

        if (user != null) {
            dataBaseReference.child(user.uid).child("name").get()
                .addOnSuccessListener { displayNameSnapshot ->
                    if (displayNameSnapshot.exists()) {
                        val displayName = displayNameSnapshot.getValue(String::class.java)
                        insusername.text = displayName
                    } else {
                        insusername.text = "No display name available"
                    }
                }
        }


        if (user != null) {
            dataBaseReference.child(user.uid).child("email").get()
                .addOnSuccessListener { displayEmailSnapshot ->
                    if (displayEmailSnapshot.exists()) {
                        val displayEmail = displayEmailSnapshot.getValue(String::class.java)
                        insuseremail.text = displayEmail
                    } else {
                        insuseremail.text = "No display name available"
                    }
                }
        }


        if (user != null) {
            dataBaseReference.child(user.uid).child("userid").get()
                .addOnSuccessListener { displayIdSnapshot ->
                    if (displayIdSnapshot.exists()) {
                        val displayId = displayIdSnapshot.getValue(String::class.java)
                        insuserid.text = displayId
                    } else {
                        insuserid.text = "No display available"
                    }
                }
        }


        if (user != null) {
            dataBaseReference.child(user.uid).child("userplace").get()
                .addOnSuccessListener { displayPlaceSnapshot ->
                    if (displayPlaceSnapshot.exists()) {
                        val displayPlace = displayPlaceSnapshot.getValue(String::class.java)
                        insuserplace.text = displayPlace
                    } else {
                        insuserplace.text = "No display available"
                    }
                }
        }


        if (user != null) {
            dataBaseReference.child(user.uid).child("userpfp").get()
                .addOnSuccessListener { displayPfpSnapshot ->
                    if (displayPfpSnapshot.exists()) {
                        val displayPfp = displayPfpSnapshot.getValue(String::class.java)
                        insusername.text = displayPfp
                    } else {
                        insusername.text = "No display available"
                    }
                }
        }

        if (user != null) {
            dataBaseReference.child(user.uid).child("userphone").get()
                .addOnSuccessListener { displayPhoneSnapshot ->
                    if (displayPhoneSnapshot.exists()) {
                        val displayPhone = displayPhoneSnapshot.getValue(String::class.java)
                        insusername.text = displayPhone
                    } else {
                        insusername.text = "No display available"
                    }
                }
        }


//                if (it.exists()){
//
//
////                    dialog.dismiss()
////
////                    binding.username.text = username.toString()
////                    binding.useremail.text = useremail.toString()
////                    binding.userid.text = userid.toString()
////                    binding.userphone.text = userphone.toString()
////                    binding.userplace.text = userplace.toString()
//
//
//                    Glide.with(this).load(userpfp).into(binding.userpfpdisplay)
//
//                }
//            }
//        }
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

//        // Initialize auth
//        auth = FirebaseAuth.getInstance()
//
//        // Set up your views or perform other tasks
//        // ...
//        val uid = auth.currentUser!!.uid
//
//        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")
//
//        dataBaseReference.child(uid).get().addOnSuccessListener {

//            val name = it.child("name").value.toString()
//            val userid = it.child("userid").value.toString()
//            val userplace = it.child("userplace").value.toString()
//            val email = it.child("email").value.toString()
//            val userphone = it.child("userphone").value.toString()
//            val pfp = it.child("pfp")
//
//            tvusername.text = name
//            tvuserid.text = userid
//            tvuserplace.text = userplace
//            tvuseremail.text = email
//            tvuserphone.text = userphone

            // Load profile picture using Glide
//            Glide.with(this).load(pfp).into(tvuserimage)
//
//        }.addOnFailureListener {
//            Snackbar.make(view, it.toString(), Snackbar.LENGTH_SHORT).show()
//        }
//
//        binding.more.setOnClickListener {
//            val popupMenu = PopupMenu(requireContext(), binding.more)
//            popupMenu.menuInflater.inflate(R.menu.menu_profilelogout, popupMenu.menu)
//
//            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
//                when (item.itemId) {
//                    R.id.logout -> {
//                        auth.signOut()
//                        startActivity(Intent(requireActivity(), SignInActivity::class.java))
//                        requireActivity().finish()
//                    }
//                }
//                true
//            })
//            popupMenu.show()
//        }
//    }
}
