package com.example.eventhub

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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


        var hasUnsavedChanges = false // Variable to track unsaved changes


        // Function to check unsaved changes and show Toast
        fun checkUnsavedChangesAndFinish() {
            val alertDialogBuilder = AlertDialog.Builder(this)

            // Set the dialog title and message
            alertDialogBuilder.setTitle("Exit Confirmation")
            alertDialogBuilder.setMessage("Are you sure you want to exit?")

            // Set positive button
            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                // If the user clicks "Yes", close the activity or take appropriate action
                finish()
            }

            // Set negative button
            alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                // If the user clicks "No", close the dialog
                dialog.dismiss()
            }

            // Show the alert dialog
            alertDialogBuilder.create().show()
        }

        binding.backButton.setOnClickListener {
            checkUnsavedChangesAndFinish()
        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkUnsavedChangesAndFinish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

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
            auth = FirebaseAuth.getInstance()

            val currentUser = auth.currentUser

            val name = binding.etusername.text.toString()
            val email: String? = currentUser?.email
            val userid = binding.etuserid.text.toString()
            val userplace = binding.etuserplace.text.toString()
            val userphone = binding.etuserphone.text.toString()

            val selectedUri = uri

            auth = FirebaseAuth.getInstance()

            if (selectedUri == null) {
                dialog.dismiss()
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            } else {
                if (name.isNotEmpty() && email != null && userid.isNotEmpty() && userplace.isNotEmpty() && userphone.isNotEmpty() && userphone.length == 10) {

                    // Check if the user ID is already taken
                    dataBaseReference.orderByChild("userid").equalTo(userid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // User ID is already taken
                                    dialog.dismiss()
                                    Toast.makeText(
                                        this@AddProfileActivity,
                                        "User ID is already taken. Please choose a different one.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    // User ID is unique
                                    val imageFileName = "pfp/${userid}.jpg"
                                    val imageRef = storageRef.reference.child(imageFileName)

                                    imageRef.putFile(selectedUri)
                                        .addOnSuccessListener { task ->
                                            task.metadata!!.reference!!.downloadUrl
                                                .addOnSuccessListener { imageUrl ->
                                                    val user = User(
                                                        name,
                                                        email,
                                                        userid,
                                                        userplace,
                                                        userphone,
                                                        pfp = imageUrl.toString()
                                                    )

                                                    val uid = auth.currentUser?.uid
                                                    if (uid != null) {
                                                        dataBaseReference.child(uid).setValue(user)
                                                            .addOnSuccessListener {
                                                                Toast.makeText(
                                                                    this@AddProfileActivity,
                                                                    "User data saved successfully",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                                dialog.dismiss()

                                                                val intent = Intent(
                                                                    this@AddProfileActivity,
                                                                    MainActivity::class.java
                                                                )
                                                                startActivity(intent)
                                                                finish()
                                                            }
                                                            .addOnFailureListener { err ->
                                                                dialog.dismiss()
                                                                Toast.makeText(
                                                                    this@AddProfileActivity,
                                                                    "Error ${err.message}",
                                                                    Toast.LENGTH_LONG
                                                                ).show()
                                                            }
                                                    }
                                                }
                                        }
                                        .addOnFailureListener {
                                            dialog.dismiss()
                                            Toast.makeText(
                                                this@AddProfileActivity,
                                                "Failed to upload image",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnProgressListener { taskSnapshot ->
                                            // Optional: Track progress
                                            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                                            // You can show this progress to the user
                                        }
                                        .addOnPausedListener {
                                            // Upload task was paused
                                        }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                dialog.dismiss()
                                Toast.makeText(
                                    this@AddProfileActivity,
                                    "Error checking user ID uniqueness",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    dialog.dismiss()
                    Toast.makeText(
                        this,
                        "Fill in all the fields or check the user phone length",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

