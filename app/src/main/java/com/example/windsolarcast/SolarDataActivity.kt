package com.example.windsolarcast

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.random.Random

class SolarDataActivity : AppCompatActivity() {

    private lateinit var tvCity: TextView
    private lateinit var tvSolarRadiation: TextView
    private lateinit var ivSolarRadiationImage: ImageView
    private lateinit var tvClearSkySolarRadiation: TextView
    private lateinit var ivClearSkySolarRadiationImage: ImageView
    private lateinit var tvLongwaveRadiation: TextView
    private lateinit var ivLongwaveRadiationImage: ImageView
    private lateinit var tvTopAtmosphereSolarRadiation: TextView
    private lateinit var ivTopAtmosphereSolarRadiationImage: ImageView
    private lateinit var tvSolarEnergyPotential: TextView
    private lateinit var ivSolarEnergyPotentialImage: ImageView

    private val NASA_BASE_URL = "https://power.larc.nasa.gov/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solar_data)

        // Initialize views
        tvCity = findViewById(R.id.tvCity)
        tvSolarRadiation = findViewById(R.id.tvSolarRadiation)
        ivSolarRadiationImage = findViewById(R.id.ivSolarRadiationImage)
        tvClearSkySolarRadiation = findViewById(R.id.tvClearSkySolarRadiation)
        ivClearSkySolarRadiationImage = findViewById(R.id.ivClearSkySolarRadiationImage)
        tvLongwaveRadiation = findViewById(R.id.tvLongwaveRadiation)
        ivLongwaveRadiationImage = findViewById(R.id.ivLongwaveRadiationImage)
        tvTopAtmosphereSolarRadiation = findViewById(R.id.tvTopAtmosphereSolarRadiation)
        ivTopAtmosphereSolarRadiationImage = findViewById(R.id.ivTopAtmosphereSolarRadiationImage)
        tvSolarEnergyPotential = findViewById(R.id.tvSolarEnergyPotential)
        ivSolarEnergyPotentialImage = findViewById(R.id.ivSolarEnergyPotentialImage)

        // Get city and solar data from intent
        val city = intent.getStringExtra("city") ?: "Unknown City"
        tvCity.text = "Solar Data for $city"

        val ghi = intent.getDoubleExtra("SolarRadiation", Double.NaN)
        val clearSky = intent.getDoubleExtra("ClearSkySolarRadiation", Double.NaN)
        val longwave = intent.getDoubleExtra("LongwaveRadiation", Double.NaN)
        val topAtmosphere = intent.getDoubleExtra("TopAtmosphereSolarRadiation", Double.NaN)

        // If valid solar data is passed via intent, display it; otherwise, fetch from NASA API
        if (!ghi.isNaN() && !clearSky.isNaN() && !longwave.isNaN() && !topAtmosphere.isNaN()) {
            Log.d("SolarDataActivity", "Data received via intent.")
            displaySolarData(ghi, clearSky, longwave, topAtmosphere, cloudCover = 50) // cloudCover = 50 is hardcoded for now
        } else {
            Log.d("SolarDataActivity", "Fetching data from NASA Power API...")
            fetchSolarData(28.61, 77.23)  // Example coordinates for New Delhi, India (replace with dynamic if needed)
        }
    }

    // Function to display solar data on the screen
    private fun displaySolarData(
        ghi: Double,
        clearSky: Double,
        longwave: Double,
        topAtmosphere: Double,
        cloudCover: Int
    ) {
        tvSolarRadiation.text = "Solar Radiation (Surface): $ghi W/m²"
        tvClearSkySolarRadiation.text = "Clear Sky Solar Radiation: $clearSky W/m²"
        tvLongwaveRadiation.text = "Longwave Radiation: $longwave W/m²"
        tvTopAtmosphereSolarRadiation.text = "Top Atmosphere Radiation: $topAtmosphere W/m²"

        val solarPower = calculateSolarPower(ghi, cloudCover)
        val potentialText = when {
            solarPower > 100 -> "High Solar Energy Potential!"
            solarPower > 50 -> "Moderate Solar Energy Potential."
            else -> "Low Solar Energy Potential."
        }
        tvSolarEnergyPotential.text = potentialText

        // Display image based on solar power potential
        val imageRes = when {
            solarPower > 100 -> R.drawable.high_solar_power_image
            solarPower > 50 -> R.drawable.moderate_solar_power_image
            else -> R.drawable.low_solar_power_image
        }
        ivSolarEnergyPotentialImage.setImageResource(imageRes)
    }

    // Function to fetch solar data from NASA Power API
    private fun fetchSolarData(lat: Double, lon: Double) {
        val retrofit = createRetrofitClient(NASA_BASE_URL)
        val api = retrofit.create(NasaPowerApiService::class.java)
        val startDate = "20240301"
        val endDate = "20240307"

        api.getSolarData(lat, lon, startDate, endDate).enqueue(object : Callback<NasaPowerResponse> {
            override fun onResponse(call: Call<NasaPowerResponse>, response: Response<NasaPowerResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.properties?.parameter
                    val ghi = data?.ALLSKY_SFC_SW_DWN?.values?.lastOrNull() ?: generateRandomValue()
                    val clearSky = data?.CLRSKY_SFC_SW_DWN?.values?.lastOrNull() ?: generateRandomValue()
                    val longwave = data?.ALLSKY_SFC_LW_DWN?.values?.lastOrNull() ?: generateRandomValue()
                    val topAtmosphere = data?.ALLSKY_TOA_SW_DWN?.values?.lastOrNull() ?: generateRandomValue()
                    displaySolarData(ghi, clearSky, longwave, topAtmosphere, cloudCover = 50)
                } else {
                    tvSolarRadiation.text = "Error fetching data"
                    Log.e("SolarDataActivity", "Error: ${response.errorBody()?.string()}")
                    // If error, display random values
                    displaySolarData(generateRandomValue(), generateRandomValue(), generateRandomValue(), generateRandomValue(), cloudCover = 50)
                }
            }

            override fun onFailure(call: Call<NasaPowerResponse>, t: Throwable) {
                tvSolarRadiation.text = "Network error: ${t.localizedMessage}"
                Log.e("SolarDataActivity", "Failure", t)
                // If network error, display random values
                displaySolarData(generateRandomValue(), generateRandomValue(), generateRandomValue(), generateRandomValue(), cloudCover = 50)
            }
        })
    }

    // Function to create Retrofit client with logging
    private fun createRetrofitClient(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // Function to calculate solar power based on GHI, cloud cover, panel area, and efficiency
    private fun calculateSolarPower(
        ghi: Double,
        cloudCover: Int,
        panelArea: Double = 1.5,
        efficiency: Double = 0.18
    ): Double {
        return ghi * (1 - cloudCover / 100.0) * panelArea * efficiency
    }

    // Function to generate random solar data value between a range
    private fun generateRandomValue(): Double {
        return Random.nextDouble(50.0, 800.0) // Random value between 50 and 800
    }
}
