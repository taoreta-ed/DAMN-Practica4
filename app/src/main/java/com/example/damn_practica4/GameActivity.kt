package com.example.damn_practica4

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.damn_practica4.databinding.ActivityGameBinding
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var imageUris: List<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve image URIs passed from ImageSelectionActivity
        val uriStrings = intent.getStringArrayExtra("IMAGE_URIS")
        if (uriStrings != null && uriStrings.isNotEmpty()) {
            imageUris = uriStrings.map { Uri.parse(it) }
            Log.d("GameActivity", "Received ${imageUris.size} image URIs.")
            Toast.makeText(this, "Game starting with ${imageUris.size} images!", Toast.LENGTH_SHORT).show()

            // TODO: Now, use these imageUris to set up your memory game board.
            // You will need to duplicate each URI to create pairs.
            // Example:
            // val gameImages = (imageUris + imageUris).shuffled()
            // Then, dynamically create ImageViews for each game card and load the image.
            // Remember to implement game logic (flipping, matching, scoring, etc.)

        } else {
            Toast.makeText(this, "No images selected. Returning.", Toast.LENGTH_LONG).show()
            finish() // Go back if no images are provided
        }

        // Placeholder to show images received (remove after implementing actual game board)
        binding.textViewGameStatus.text = "Received ${imageUris.size} unique images for the game."
    }
}