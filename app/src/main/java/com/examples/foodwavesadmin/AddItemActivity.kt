package com.examples.foodwavesadmin

import android.R.attr.key
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.examples.foodwavesadmin.databinding.ActivityAddItemBinding
import com.examples.foodwavesadmin.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {
    // food items details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private var foodImage: Uri? = null
    private lateinit var foodIngredient: String

    // firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //initialise firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.AddItemButtoon.setOnClickListener {
            // get data from fields
            foodName = binding.enterFoodName.text.toString().trim()
            foodPrice = binding.enterFoodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredients.text.toString().trim()

            if (!(foodName.isBlank() || foodPrice.isBlank() || foodIngredient.isBlank() || foodDescription.isBlank())) {
                uploadData()
                Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please Fill All Details", Toast.LENGTH_SHORT).show()
            }
        }
        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadData() {


        // get a reference to the "menu" node in the database
        val menuRef = database.getReference("menu")
        // generate a unique key for the new menu item
        val newItemKey = menuRef.push().key


        if (foodImage != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImage!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    //Create new menu item
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodImage = downloadUrl.toString(),
                        foodIngredient = foodIngredient,
                    )
                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "Data Uploaded Successfully", Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "Data Uploading Failed", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "Image Uploading Failed", Toast.LENGTH_SHORT).show()
                }

            }

        } else {
            Toast.makeText(this, "Please select an Image", Toast.LENGTH_SHORT).show()
        }

    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null)
            binding.selectedImage.setImageURI(uri)
        foodImage = uri
    }
}