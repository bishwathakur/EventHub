package com.example.eventhub

import android.app.Dialog
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.AddprofileBinding
import com.example.eventhub.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class AddProfileActivity : AppCompatActivity() {

    private lateinit var binding: AddprofileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    private lateinit var dialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = AddprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)







        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        dataBaseReference = FirebaseDatabase.getInstance().getReference("Users")
        binding.addButton.setOnClickListener {


            showProgressBar()
            val name = binding.etusername.text.toString()
            val email = binding.etusermail.text.toString()
            val userid = binding.etuserid.text.toString()
            val userplace = binding.etuserplace.text.toString()
            val userphone = binding.etuserphone.text.toString()

            val user = User(name, email, userid, userplace, userphone)
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
    }

    private fun showProgressBar() {

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
























