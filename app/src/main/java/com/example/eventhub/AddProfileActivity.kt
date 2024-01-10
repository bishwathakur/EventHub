package com.example.eventhub

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Button
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

class AddProfileActivity : AppCompatActivity() {

    private lateinit var binding: AddprofileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog
    private lateinit var image: ImageView

    private var uri: Uri? = null
    private lateinit var addBtn: Button

    private lateinit var dataBaseReference: DatabaseReference
    private lateinit var storageRef: FirebaseStorage

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

        image.setOnClickListener {
            galleryImage.launch("image/*")
        }

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        binding.addButton.setOnClickListener {

            if (!isFinishing && !isDestroyed) {
                dialog = Dialog(this@AddProfileActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.dialog_wait)
                dialog.setCanceledOnTouchOutside(false)

                if (!isFinishing && !isDestroyed) {
                    dialog.show()
                }
            }

            val name = binding.etusername.text.toString()
            val email = binding.etusermail.text.toString()
            val userid = binding.etuserid.text.toString()
            val userplace = binding.etuserplace.text.toString()
            val userphone = binding.etuserphone.text.toString()

            val selectedUri = uri


            if (uri == null) {
                dialog.dismiss()
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            } else {
                storageRef.getReference("pfp").child(System.currentTimeMillis().toString())
                    .putFile(selectedUri!!)
                    .addOnSuccessListener { task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener { imageurl ->
                                val userId = auth.currentUser!!.uid

                                val user = User(name, email, userid, userplace, userphone, pfp = imageurl.toString())

                                if (name.isEmpty() || email.isEmpty() || userid.isEmpty() || userplace.isEmpty() || userphone.isEmpty() || imageurl.toString().isEmpty()) {
                                    dialog.dismiss()
                                    Toast.makeText(this, "Fill in all the fields", Toast.LENGTH_SHORT).show()
                                } else if (userphone.length !=10) {
                                    dialog.dismiss()
                                    Toast.makeText(this, "Phone number invalid", Toast.LENGTH_SHORT).show()
                                } else {
                                    val userId = auth.currentUser!!.uid

                                    userId.let { uid ->
                                        val userdata = dataBaseReference.child(uid).push().key
                                        val Huha = dataBaseReference.child(uid)
                                        userdata?.let { key ->
                                            Huha.child(key).setValue(user)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        this, "User data saved successfully", Toast.LENGTH_SHORT
                                                    ).show()
                                                    dialog.dismiss()
                                                    val intent = Intent

                                                }
                                                .addOnFailureListener {
                                                    dialog.dismiss()
                                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                                }
                            }
                    }
            }

            binding.backButton.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    finish()
                }
            }

            onBackPressedDispatcher.addCallback(this, callback)
        }
    }
}
