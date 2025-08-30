package com.examples.foodwavesadmin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.examples.foodwavesadmin.databinding.ActivityCreateUserBinding

class CreateUserActivity : AppCompatActivity() {
    private val binding: ActivityCreateUserBinding by lazy {
        ActivityCreateUserBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }

    }
}