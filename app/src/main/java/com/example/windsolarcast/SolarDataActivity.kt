package com.example.windsolarcast // Updated package name

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text

class SolarDataActivity : AppCompatActivity() {
    private lateinit var tvCity: TextView
    private lateinit var tvSolarRadiation: TextView
    private lateinit var tvClearSkySolar: TextView
    // ... declare other TextViews for solar data

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solar_data) // Updated layout file

        // Initialize TextViews using findViewById
        tvCity = findViewById(R.id.tvCity)
        tvSolarRadiation = findViewById(R.id.tvSolarRadiation)
        tvClearSkySolar = findViewById(R.id.tvClearSkySolar)
        // ... initialize other TextViews

        // Get data passed from MainActivity (city, solar data)
        val city = intent.getStringExtra("city") ?: ""
        val ghi = intent.getDoubleExtra("ghi", 0.0)
        val clearSkySolar = intent.getDoubleExtra("clearSkySolar", 0.0)
        // ... get other solar data

        // Update UI elements in activity_solar_data.xml
        tvCity.text = city
        tvSolarRadiation.text = "Solar Radiation: $ghi W/m²"
        tvClearSkySolar.text = "Clear Sky Solar: $clearSkySolar W/m²"
        // ... update other solar-related views
    }
}