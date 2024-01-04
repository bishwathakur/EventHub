package com.example.eventhub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.eventhub.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage

class Profile : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference : DatabaseReference
    private lateinit var image: ImageView
    private lateinit var btnBrowse : ImageView
    private lateinit var btnUpload : Button
    private lateinit var storageRef : FirebaseStorage
    private lateinit var uri : Uri


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


        storageRef = FirebaseStorage.getInstance()
        image = binding.dp
        btnBrowse = binding.browse
        btnUpload = binding.upload


        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                image.setImageURI(it)
                uri = it!!
            })

        btnBrowse.setOnClickListener{
            galleryImage.launch("image/*")
            btnUpload.visibility = View.VISIBLE
        }


        btnUpload.setOnClickListener{
            storageRef.getReference("pfp").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { it ->
                            val userid = FirebaseAuth.getInstance().currentUser!!.uid

                            val mapImage = mapOf(
                                "url" to it.toString()
                            )
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            databaseReference.child(userid).setValue(mapImage)
                                .addOnCompleteListener {
                                    Snackbar.make(view, "Successful", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener{ error ->
                                    Snackbar.make(view, it.toString(), Toast.LENGTH_SHORT ).show()
                                }
                        }
                }
            btnUpload.visibility = View.INVISIBLE


        }

        // Initialize your views here
        val tvusername: TextView = binding.username
        val tvuserid: TextView = binding.userid
        val tvuserplace: TextView = binding.userplace
        val tvuseremail: TextView = binding.useremail
        val tvuserphone: TextView = binding.userphone



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


        }.addOnFailureListener {
            Snackbar.make(view, it.toString(), Snackbar.LENGTH_SHORT).show()
        }

        binding.more.setOnClickListener {
            val popupMenu = PopupMenu(activity, binding.more)
            popupMenu.menuInflater.inflate(R.menu.menu_profilelogout, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.logout -> {
                        auth.signOut()
                        startActivity(Intent(activity, SignInActivity::class.java))
                    }
                }
                true
            })
            popupMenu.show()
        }
    }
}
