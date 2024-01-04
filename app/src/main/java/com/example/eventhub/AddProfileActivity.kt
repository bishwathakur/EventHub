package com.example.eventhub

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eventhub.databinding.AddprofileBinding
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import android.view.*
import android.widget.Button


class AddProfileActivity : AppCompatActivity() {

    private lateinit var binding: AddprofileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private lateinit var image: ImageView

    private lateinit var uri : Uri
    private lateinit var addBtn : Button

    private lateinit var dataBaseReference : DatabaseReference
    private lateinit var storageRef : FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = AddprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storageRef = FirebaseStorage.getInstance()
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")

        image = binding.etuserpfp
        addBtn = binding.addButton

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                image.setImageURI(it)
                uri = it!!
            })

        image.setOnClickListener{
            galleryImage.launch("image/*")
        }




        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        binding.addButton.setOnClickListener {

            showProgressBar()


            storageRef.getReference("pfp").child(System.currentTimeMillis().toString())
                .putFile(uri)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener {
                            val userId = auth.currentUser!!.uid

                            val mapImage = mapOf(
                                "userpfp" to it.toString()
                            )
                            val databaseReference =
                                FirebaseDatabase.getInstance().getReference("Users")

                            databaseReference.child(userId).setValue(mapImage)

                        }

                }
            val name = binding.etusername.text.toString()
            val email = binding.etusermail.text.toString()
            val userid = binding.etuserid.text.toString()
            val userplace = binding.etuserplace.text.toString()
            val userpfp = uri.toString()
            val userphone = binding.etuserphone.text.toString()


            do {

                if (userphone.matches(Regex("\\d{10}"))) {


                    Toast.makeText(this@AddProfileActivity, "Phone number is valid", Toast.LENGTH_SHORT).show()
                } else {


                    Toast.makeText(this@AddProfileActivity, "Invalid phone number", Toast.LENGTH_SHORT).show()

                }
            }while (!userphone.matches(Regex("\\d{10}")))




            val user = User(name, email, userid, userplace, userphone, userpfp)
            if (uid != null) {

                dataBaseReference.child(uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {

                        hideProgessBar()
                        Toast.makeText(this@AddProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
            binding.backButton.setOnClickListener {
                // Handle back button press
                onBackPressedDispatcher.onBackPressed()
            }

            // Create an onBackPressedCallback
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }

            // Add the callback to the onBackPressedDispatcher
            onBackPressedDispatcher.addCallback(this, callback)
        }


    private fun showProgressBar(){

        dialog = Dialog(this@AddProfileActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }
    private fun hideProgessBar() {
        dialog.dismiss()
    }



}
























