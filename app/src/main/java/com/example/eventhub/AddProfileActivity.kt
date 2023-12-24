package com.example.eventhub

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.AddprofileBinding
import com.example.eventhub.ui.theme.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



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

            val user = User(name, email, userid, userplace)
            if (uid != null) {

                dataBaseReference.child(uid).setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {

                        hideProgessBar()
                        Toast.makeText(this@AddProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
























