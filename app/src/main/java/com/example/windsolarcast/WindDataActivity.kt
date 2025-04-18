package com.example.windsolarcast

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text

class WindDataActivity : AppCompatActivity() {

    private lateinit var tvCity: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvWindSpeed: TextView
    private lateinit var tvCloudCover: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wind_data)

        tvCity = findViewById(R.id.tvCity)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvWindSpeed = findViewById(R.id.tvWindSpeed)
        tvCloudCover = findViewById(R.id.tvCloudCover)

        val city = intent.getStringExtra("city") ?: "Unknown City"
        val temperature = intent.getDoubleExtra("temperature", 0.0)
        val windSpeed = intent.getFloatExtra("windSpeed", 0.0f)
        val cloudCover = intent.getIntExtra("cloudCover", 0)

        tvCity.text = "Wind Data for $city"
        tvTemperature.text = "Temperature: ${temperature}°C"
        tvWindSpeed.text = "Wind Speed: $windSpeed m/s"
        tvCloudCover.text = "Cloud Cover: $cloudCover%"

        // You can add more UI elements to display wind power potential
        // or other wind-related information calculated from the available data.
    }
}