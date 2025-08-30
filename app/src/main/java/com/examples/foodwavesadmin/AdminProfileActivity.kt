package com.examples.foodwavesadmin

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.examples.foodwavesadmin.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.saveInformation.setOnClickListener {
            updateUserData()
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("user")


        binding.name.isEnabled = false
        binding.address.isEnabled = false
        binding.email.isEnabled = false
        binding.phone.isEnabled = false
        binding.password.isEnabled = false
        binding.saveInformation.isEnabled = false

        var isEnable = false
        binding.editButton.setOnClickListener {
            isEnable = !isEnable
            binding.name.isEnabled = isEnable
            binding.address.isEnabled = isEnable
            binding.email.isEnabled = isEnable
            binding.phone.isEnabled = isEnable
            binding.password.isEnabled = isEnable
            if (isEnable) {
                binding.name.requestFocus()
            }
            binding.saveInformation.isEnabled = isEnable
        }
        retrieveUserData()

    }


    private fun retrieveUserData() {

        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var ownerName = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var password = snapshot.child("password").getValue()
                        var address = snapshot.child("address").getValue()
                        var phone = snapshot.child("phone").getValue()
                        setDtaToTextView(ownerName, email, password, address, phone)

                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        }
    }

    private fun setDtaToTextView(
        ownerName: Any?,
        email: Any?,
        password: Any?,
        address: Any?,
        phone: Any?
    ) {
        binding.name.setText(ownerName.toString())
        binding.email.setText(email.toString())
        binding.password.setText(password.toString())
        binding.address.setText(address.toString())
        binding.phone.setText(phone.toString())

    }

    private fun updateUserData() {

        var updateName = binding.name.text.toString()
        var updateEmail = binding.email.text.toString()
        var updatePassword = binding.password.text.toString()
        var updateAddress = binding.address.text.toString()
        var updatePhone = binding.phone.text.toString()
        val currentUserUid = auth.currentUser?.uid
        Log.d("AdminProfile", "Writing to /user/$currentUserUid")

        if (currentUserUid == null) {
            Log.e("AdminProfile", "UID is null")
            return
        }
        if (currentUserUid != null) {

            val userReference = adminReference.child(currentUserUid)
            Log.d("AdminProfile", "userReference = ${userReference.toString()}")
            userReference.child("name").setValue(updateName)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("AdminProfile", "Name saved successfully")
                    } else {
                        Log.e("AdminProfile", "Failed to save name: ${task.exception?.message}")
                    }
                }
            userReference.child("email").setValue(updateEmail)
            userReference.child("password").setValue(updatePassword)
            userReference.child("phone").setValue(updatePhone)
            userReference.child("address").setValue(updateAddress)
            Toast.makeText(this, "Profile Updated Successfully ", Toast.LENGTH_SHORT).show()

//update the password adn email for authentication
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
        } else {
            Toast.makeText(this, "Profile Updation Failed ", Toast.LENGTH_SHORT).show()

        }

    }
}