package com.zybooks.workingwithdata

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var mainActivityButton = findViewById<Button>(R.id.mainActivityButton)
        mainActivityButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        var nasaButton = findViewById<Button>(R.id.nasaButton)
        nasaButton.setOnClickListener {
            var intent = Intent(this, NasaAPI::class.java)
            startActivity(intent)
        }
        var fireballsButton = findViewById<Button>(R.id.fireballsButton)
        fireballsButton.setOnClickListener {
            var intent = Intent(this, Fireballs::class.java)
            startActivity(intent)
        }

    }
}