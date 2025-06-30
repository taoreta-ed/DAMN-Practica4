package com.example.damn_practica4

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button // Asegúrate de que esta importación esté presente
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.damn_practica4.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var sharedPreferences: SharedPreferences // Para guardar la preferencia del tema

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        // Cargar y aplicar el tema guardado al iniciar la actividad
        val savedThemeMode = sharedPreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)

        // Configuración existente para el botón de selección de imágenes
        binding.buttonSelectImages.setOnClickListener {
            val intent = Intent(this, ImageSelectionActivity::class.java)
            startActivity(intent)
        }

        // Nuevo: Configuración del botón para cambiar el tema
        binding.buttonToggleTheme.setOnClickListener {
            val currentNightMode = AppCompatDelegate.getDefaultNightMode()
            val newNightMode = if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO // Cambiar a modo claro
            } else {
                AppCompatDelegate.MODE_NIGHT_YES // Cambiar a modo oscuro
            }

            // Aplicar el nuevo tema
            AppCompatDelegate.setDefaultNightMode(newNightMode)

            // Guardar la nueva preferencia de tema
            sharedPreferences.edit().putInt("theme_mode", newNightMode).apply()

            // Opcional: Recrear la actividad para aplicar el cambio de tema de inmediato.
            // Si no haces esto, el cambio de tema solo se verá completamente al reiniciar la app o la actividad.
            recreate()
        }
    }
}