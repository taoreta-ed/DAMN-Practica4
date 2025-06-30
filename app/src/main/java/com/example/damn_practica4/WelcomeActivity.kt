package com.example.damn_practica4

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.damn_practica4.databinding.ActivityWelcomeBinding // Will be generated after creating XML

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStartGame.setOnClickListener {
            // Navigate to the Image Selection screen
            val intent = Intent(this, ImageSelectionActivity::class.java)
            startActivity(intent)
        }
    }
}