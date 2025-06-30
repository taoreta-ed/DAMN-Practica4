package com.example.damn_practica4

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.damn_practica4.databinding.ActivityGameBinding
import java.util.*
import kotlin.collections.ArrayList

// Extension function for converting dp to px
fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var imageUris: List<Uri> // Original unique image URIs
    private lateinit var gameCards: ArrayList<Uri> // Stores the duplicated and shuffled URIs for the game board
    private var flippedCards = mutableListOf<ImageView>()
    private var flippedCardUris = mutableListOf<Uri>()
    private var matchesFound = 0
    private var isClickEnabled = true // To prevent multiple clicks during comparison delay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriStrings = intent.getStringArrayExtra("IMAGE_URIS")
        if (uriStrings != null && uriStrings.isNotEmpty()) {
            imageUris = uriStrings.map { Uri.parse(it) }

            if (imageUris.size < 4) {
                Toast.makeText(this, "Please select at least 4 images to play the 8-card game.", Toast.LENGTH_LONG).show()
                finish()
                return
            }

            setupGameBoard()
        } else {
            Toast.makeText(this, "No images selected. Returning.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupGameBoard() {
        val uniqueImagesForGame = imageUris.take(4) // Take only the first 4 unique images for 4 pairs (8 cards)

        gameCards = ArrayList()
        for (uri in uniqueImagesForGame) {
            gameCards.add(uri)
            gameCards.add(uri)
        }
        gameCards.shuffle() // Shuffle the cards

        val columnCount = 4 // Fixed to 4 columns for a 4x2 layout
        val rowCount = (gameCards.size / columnCount) // For 8 cards and 4 columns, this will be 2 rows

        binding.gameBoardLayout.columnCount = columnCount
        binding.gameBoardLayout.rowCount = rowCount

        for (i in 0 until gameCards.size) {
            val cardImageView = ImageView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    // *** IMPORTANT CHANGE: Set width and height to 0dp for GridLayout weights to work effectively ***
                    width = 0
                    height = 0
                    setMargins(8.dpToPx(this@GameActivity), 8.dpToPx(this@GameActivity), 8.dpToPx(this@GameActivity), 8.dpToPx(this@GameActivity))
                    columnSpec = GridLayout.spec(i % columnCount, 1f) // Weight 1f for even column distribution
                    rowSpec = GridLayout.spec(i / columnCount, 1f)    // Weight 1f for even row distribution
                }
                scaleType = ImageView.ScaleType.CENTER_CROP // Ensure image scales well within the ImageView
                setBackgroundResource(R.drawable.card_back_placeholder) // Set initial card back
                tag = gameCards[i].toString() // Store the actual image URI in the tag for comparison
            }

            cardImageView.setOnClickListener {
                if (isClickEnabled && cardImageView !in flippedCards && cardImageView.alpha == 1f) {
                    flipCard(cardImageView, Uri.parse(cardImageView.tag.toString()))
                }
            }
            binding.gameBoardLayout.addView(cardImageView)
        }

        // *** REMOVED: The .post block for manual sizing is no longer needed
        // *** when using 0dp width/height with weights.
        // *** GridLayout will handle the distribution and sizing automatically.

        binding.textViewGameStatus.text = "Game started! Find the pairs."
        Log.d("GameActivity", "Game board set up with ${gameCards.size} cards.")
    }

    private fun flipCard(cardImageView: ImageView, cardUri: Uri) {
        cardImageView.setImageURI(cardUri)

        flippedCards.add(cardImageView)
        flippedCardUris.add(cardUri)

        if (flippedCards.size == 2) {
            isClickEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                checkForMatch()
            }, 1000)
        }
    }

    private fun checkForMatch() {
        val card1 = flippedCards[0]
        val card2 = flippedCards[1]
        val uri1 = flippedCardUris[0]
        val uri2 = flippedCardUris[1]

        if (uri1 == uri2) {
            Toast.makeText(this, "Match found!", Toast.LENGTH_SHORT).show()
            card1.alpha = 0.5f
            card2.alpha = 0.5f
            matchesFound++

            if (matchesFound == gameCards.size / 2) {
                Toast.makeText(this, "Congratulations! You found all pairs!", Toast.LENGTH_LONG).show()
                binding.textViewGameStatus.text = "¡Juego completado!"
            }
        } else {
            Toast.makeText(this, "No match. Try again!", Toast.LENGTH_SHORT).show()
            card1.setImageDrawable(null)
            card2.setImageDrawable(null)
            card1.setBackgroundResource(R.drawable.card_back_placeholder)
            card2.setBackgroundResource(R.drawable.card_back_placeholder)
        }

        flippedCards.clear()
        flippedCardUris.clear()
        isClickEnabled = true
    }
}