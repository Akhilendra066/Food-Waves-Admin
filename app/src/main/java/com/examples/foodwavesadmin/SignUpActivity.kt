package com.examples.foodwavesadmin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.examples.foodwavesadmin.databinding.ActivitySignUpBinding
import com.examples.foodwavesadmin.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var nameOfRestaurant: String
    private lateinit var database: DatabaseReference


    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //initialise Firebase Auth
        auth = Firebase.auth
        // initialise Firebase database
        database = Firebase.database.reference


        binding.creatAccountButton.setOnClickListener {
            //get text from edit text
            email = binding.emailOrPhone.text.toString().trim()
            nameOfRestaurant = binding.restaurantName.text.toString().trim()
            userName = binding.name.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (userName.isBlank()||nameOfRestaurant.isBlank()||email.isBlank()||password.isBlank()){
                Toast.makeText(this,"Please fill all details", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email,password)
            }

        }
        binding.alreadyHaveAccountButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val locationList = arrayOf("Lucknow", "Fatehpur", "Mumbai", "Kanpur")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

    }

    private fun createAccount(email:String, password:String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account","Create Account:Failed",task.exception)
            }
        }
    }
// save data into database
    private fun saveUserData() {
        email = binding.emailOrPhone.text.toString().trim()
        nameOfRestaurant = binding.restaurantName.text.toString().trim()
        userName = binding.name.text.toString().trim()
        password = binding.password.text.toString().trim()
        val user = UserModel(userName,nameOfRestaurant, email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
    // save user data Firebase database
        database.child("user").child(userId).setValue(user)
    }
}