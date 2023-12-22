package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.AddprofileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.lang.ref.PhantomReference

class CmpRegDetailsActivity : AppCompatActivity() {

    private lateinit var binding: AddprofileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = AddprofileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        dataBaseReference = FirebaseAuth.getInstance().getRefernce("Users")

        binding.cmpDetailsReg.setOnClickListener {
            val sb = StringBuilder()
            val services=sb.toString()

            if(checkEditText()){
                val i = Intent(applicationContext, LocationRegSelectActivity::class.java)
                i.putExtra("phone", intent.getStringExtra("phone").toString())
                i.putExtra("cname", binding.cmpRegName.text.toString())
                i.putExtra("email", binding.cmpRegMail.text.toString())
                i.putExtra("yoe", binding.cmpRegDoe.text.toString())
                i.putExtra("cin", binding.cmpRegCin.text.toString())
                i.putExtra("desc", binding.cmpRegDes.text.toString())
                i.putExtra("service", services)
                startActivity(i)
            }

        }
    }

    fun checkEditText():Boolean{
        var status=true
        if(binding.cmpRegName.text.toString().isEmpty()){
            binding.cmpNameLayout.error= "Check Fields & Try Again"
            status=false
        }
        if(binding.cmpRegMail.text.toString().isEmpty()){
            binding.cmpMailLayout.error= "Check Fields & Try Again"
            status=false
        }
        if(binding.cmpRegDoe.text.toString().isEmpty()){
            binding.cmpDoeLayout.error= "Check Fields & Try Again"
            status=false
        }
        if(binding.cmpRegCin.text.toString().isEmpty()){
            binding.cmpCinLayout.error= "Check Fields & Try Again"
            status=false
        }
        if(binding.cmpRegDes.text.toString().isEmpty()){
            binding.cmpDesLayout.error= "Check Fields & Try Again"
            status=false
        }
        return status
    }

}