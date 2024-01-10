package com.example.eventhub

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.RequestManager
import com.example.eventhub.databinding.ActivityAddeventBinding

import com.example.eventhub.models.EventViewModel
import com.example.eventhub.models.Post
import com.example.eventhub.models.User
import com.example.eventhub.utils.Status
import com.example.eventhub.utils.Resource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AddEventActivity : AppCompatActivity(){

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101

    //image pick constants
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103

    //video pick constants
    private val VIDEO_PICK_CAMERA_CODE = 104
    private val VIDEO_PICK_GALLERY_CODE = 105


    var imageUri: Uri? = null


    private val permissionReadMediaImages = "android.permission.READ_MEDIA_IMAGES"
    private val permissionReadMediaVideo = "android.permission.READ_MEDIA_VIDEO"


    private val viewModel by viewModels<EventViewModel>()

    lateinit var thisUser : User


    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageRef: FirebaseStorage
    private lateinit var uri: Uri
    private lateinit var Eventpic: ImageView
    private lateinit var AddEvent: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog

    @Inject
    lateinit var glide: RequestManager


    private lateinit var myContext: Context

    private lateinit var binding: ActivityAddeventBinding

    private lateinit var cameraImageLauncher: ActivityResultLauncher<Uri>
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setupActivityResultLaunchers()




        viewModel.getDataForCurrentUser()


        //For use in main home activity fragment



//        viewModel.currentUserLiveData.observe(this) {
//            when (it.status) {
//                Status.SUCCESS -> {
//                    thisUser = it.data!!
//                    glide.load(thisUser.pfp).into(binding.publishMyImage)
//                    binding.publishMyName.text = thisUser.name
//                }
//                Status.ERROR -> {
//                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//                }
//
//                else -> {}
//            }
//        }
        binding.insEventPhoto.setOnClickListener{

            imagePickDialog()
        }

        binding.insEventdate.setOnClickListener {
            showDatePickerDialog()
        }


        binding.addeventbtn.setOnClickListener {
            val eventname =binding.insEventname.text.toString()
            val eventdate = binding.insEventdate.text.toString()
            val eventvenue= binding.insEventvenue.text.toString()
            val eventby= binding.insEventby.text.toString()
            if (eventname.isEmpty() && eventdate.isEmpty() && eventvenue.isEmpty() && eventby.isEmpty()){
                Toast.makeText(this, "Fill all the Fields!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            var postAttachment: String = ""
            if (imageUri == null) {
                //just article
                Toast.makeText(this@AddEventActivity, "Insert Event Image!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            } else if (imageUri != null) {
                //image
                postAttachment = imageUri.toString()
            }


            val post = Post(
                thisUser.userid, thisUser.name, thisUser.email, thisUser.pfp, eventname,
                eventdate, eventvenue, eventby, postAttachment
            )


            viewModel.uploadPost(post)
            viewModel.postLiveData.observe(this) {
                when (it.status) {
                    Status.SUCCESS -> {
                        Toast.makeText(myContext, "Post Published", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    Status.ERROR -> {
                        Toast.makeText(myContext, "${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }




        }


    }

    private fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        binding.insEventdate.setText(formattedDate)
    }



    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)

        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            onDateSet(null, year, month, dayOfMonth)
        }, year, month, date)

        datePickerDialog.show()
    }

    private fun setupActivityResultLaunchers() {
        // For picking image from gallery
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.insEventPhoto.setImageURI(imageUri)
            }
        }

        // For capturing image from camera
        cameraImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
            if (success) {
                binding.insEventPhoto.setImageURI(imageUri)
            }
        }
    }

    private fun imagePickDialog() {
        val options = arrayOf("Camera", "Gallery")
        //dialog
        val builder = AlertDialog.Builder(this)

        //title
        builder.setTitle("Pick image From ?!")
        builder.setCancelable(false)

        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                //camera clicked

                imagePickCamera()

            } else if (which == 1) {
                // gallery clicked

                imagePickGallery()

            }

        }

        //create show dialog
        builder.create().show()
    }

    private fun imagePickCamera() {
        val photoUri: Uri = createImageUri()
        imageUri = photoUri
        cameraImageLauncher.launch(photoUri)
    }


    private fun imagePickGallery() {
        imagePickerLauncher.launch("image/*")
    }
    private fun createImageUri(): Uri {
        // Ensure the directory for storing the image exists
        val imagesFolder = File(getExternalFilesDir(null), "images")
        if (!imagesFolder.exists()) imagesFolder.mkdirs()

        // Create a file for the image
        val file = File(imagesFolder, "post_image_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", file)
    }


}
