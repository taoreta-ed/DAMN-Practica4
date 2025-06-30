package com.example.damn_practica4 // Corrected package name

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.damn_practica4.databinding.ActivityMainBinding // Corrected import for binding class
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // For View Binding
    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Initialize View Binding
        setContentView(binding.root)

        // Ensure you have added these permissions to your AndroidManifest.xml:
        // <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

        checkStoragePermission()

        // Example: Display internal storage path
        val internalStoragePath = filesDir.absolutePath
        binding.textViewStatus.text = "Internal Storage: $internalStoragePath"
    }

    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For Android 11 (API 30) and above, WRITE_EXTERNAL_STORAGE has a different behavior.
            // For simplicity on Android 8 (API 26), these permissions are sufficient for common file access.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Storage permissions already granted.")
                setupFileManager()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    STORAGE_PERMISSION_CODE
                )
            }
        } else {
            Log.d("MainActivity", "Storage permissions not required for this Android version.")
            setupFileManager()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
                setupFileManager()
            } else {
                Toast.makeText(this, "Storage Permission Denied. File operations may be limited.", Toast.LENGTH_LONG).show()
                // Handle denied permission, e.g., disable file operations or show a message
            }
        }
    }

    private fun setupFileManager() {
        // This is where you will implement the core file manager logic.
        // You can start by listing files from a specific directory, e.g., downloads or documents.
        Log.d("MainActivity", "Setting up file manager logic.")

        // Example: List files in the Downloads directory (requires permissions)
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (downloadsDir.exists() && downloadsDir.isDirectory) {
            val files = downloadsDir.listFiles()
            if (files != null) {
                Log.d("MainActivity", "Files in Downloads directory:")
                for (file in files) {
                    Log.d("MainActivity", "  - ${file.name} (Directory: ${file.isDirectory})")
                }
            } else {
                Log.d("MainActivity", "No files found in Downloads directory or access denied.")
            }
        } else {
            Log.d("MainActivity", "Downloads directory does not exist or is not a directory.")
        }

        // TODO: Design your UI (activity_main.xml) to display file lists.
        // TODO: Implement navigation through directories.
        // TODO: Add logic to open and view different file types (text, JSON, XML, images).
        // TODO: Implement file operations like copy, move, rename, delete.
        // TODO: Implement history, favorites, and cache management.
    }
}