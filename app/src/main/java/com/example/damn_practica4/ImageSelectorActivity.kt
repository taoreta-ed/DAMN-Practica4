package com.example.damn_practica4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.damn_practica4.databinding.ActivityImageSelectionBinding
import androidx.core.app.ActivityCompat

class ImageSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageSelectionBinding
    private val PICK_IMAGE_REQUEST_CODE = 1
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1001

    // Store selected image URIs
    private val selectedImageUris = mutableListOf<Uri>()

    // ActivityResultLauncher for picking images
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ActivityResultLauncher
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    if (data.clipData != null) { // Multiple images selected
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            addSelectedImage(imageUri)
                        }
                    } else if (data.data != null) { // Single image selected
                        val imageUri = data.data!!
                        addSelectedImage(imageUri)
                    }
                }
            }
        }

        binding.buttonSelectImages.setOnClickListener {
            checkAndRequestPermissionsAndPickImages()
        }

        binding.buttonPlayGame.setOnClickListener {
            if (selectedImageUris.size >= 4) { // Ensure at least 4 images (for 4 pairs)
                // Pass the selected image URIs to the GameActivity
                val intent = Intent(this, GameActivity::class.java).apply {
                    // Convert Uri list to String list if needed for easy Intent passing
                    val uriStrings = selectedImageUris.map { it.toString() }.toTypedArray()
                    putExtra("IMAGE_URIS", uriStrings)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select at least 4 images for the game.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAndRequestPermissionsAndPickImages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 13 (API 33) and above, granular media permissions might be used.
            // For Android 8 (your target), READ_EXTERNAL_STORAGE is sufficient for general media access.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImages()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                )
            }
        } else {
            // Permissions not required for Android versions below M (API 23)
            pickImages()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Storage Permission Granted", Toast.LENGTH_SHORT).show()
                pickImages()
            } else {
                Toast.makeText(this, "Permission denied. Cannot select images.", Toast.LENGTH_LONG).show()
                // Optionally, guide user to settings if permission is permanently denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // User denied permission and checked "Don't ask again"
                    showSettingsDialog()
                }
            }
        }
    }

    private fun showSettingsDialog() {
        // You can create a more elaborate dialog here.
        Toast.makeText(this, "Please enable storage permission in App Settings to select images.", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun pickImages() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow selecting multiple images
        }
        pickImageLauncher.launch(Intent.createChooser(intent, "Select Images for Memory Game"))
    }

    private fun addSelectedImage(uri: Uri) {
        // Prevent adding duplicate URIs, though picking from gallery usually handles this.
        if (!selectedImageUris.contains(uri)) {
            selectedImageUris.add(uri)
            Log.d("ImageSelection", "Added image: $uri. Total selected: ${selectedImageUris.size}")
            Toast.makeText(this, "Image selected! Total: ${selectedImageUris.size}", Toast.LENGTH_SHORT).show()
            updateSelectedImagesDisplay()
        }
    }

    private fun updateSelectedImagesDisplay() {
        // Dynamically add ImageView for each selected image or update a counter
        // For simplicity, let's just update a text view for now.
        binding.textViewSelectedImageCount.text = "Images selected: ${selectedImageUris.size}"

        // You could also iterate and add ImageViews to a LinearLayout:
        // binding.imagePreviewContainer.removeAllViews()
        // selectedImageUris.forEach { uri ->
        //     val imageView = ImageView(this).apply {
        //         layoutParams = LinearLayout.LayoutParams(100.dpToPx(), 100.dpToPx()).apply {
        //             marginEnd = 8.dpToPx()
        //         }
        //         scaleType = ImageView.ScaleType.CENTER_CROP
        //         setImageURI(uri)
        //     }
        //     binding.imagePreviewContainer.addView(imageView)
        // }
    }

    // Extension function for converting dp to px (optional, for UI elements)
    // fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}