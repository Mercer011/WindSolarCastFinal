package com.example.windsolarcast

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnViewWindData: Button
    private lateinit var btnViewSolarData: Button
    private lateinit var tvDefaultCity: TextView
    private lateinit var ivLocationIcon: ImageView

    private val WEATHER_API_KEY = "315f293daca93eaf2847d326cec66326"
    private val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"

    var currentTemperature: Double = 0.0
    var currentWindSpeed: Float = 0.0f
    var currentCloudCover: Int = 0

    var currentSolarRadiation: Double = 0.0
    var currentClearSkySolarRadiation: Double = 0.0
    var currentLongwaveRadiation: Double = 0.0
    var currentTopAtmosphereSolarRadiation: Double = 0.0
    var currentSolarEnergyPotential: Double = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        etCity = findViewById(R.id.etCity)
        btnSearch = findViewById(R.id.btnSearch)
        btnViewWindData = findViewById(R.id.btnViewWindData)
        btnViewSolarData = findViewById(R.id.btnViewSolarData)
        tvDefaultCity = findViewById(R.id.tvDefaultCity)
        ivLocationIcon = findViewById(R.id.ivLocationIcon)

        // Set default city
        val defaultCity = "Jalandhar"
        tvDefaultCity.text = defaultCity
        fetchWeatherData(defaultCity)

        // Set button click listeners
        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeatherData(city)
                tvDefaultCity.text = city
            } else {
                tvDefaultCity.text = "Please enter a city"
            }
        }

        btnViewWindData.setOnClickListener {
            val city = tvDefaultCity.text.toString()
            val intent = Intent(this, FourthPage::class.java)
            intent.putExtra("city", city)
            intent.putExtra("temperature", currentTemperature)
            intent.putExtra("windSpeed", currentWindSpeed)
            intent.putExtra("cloudCover", currentCloudCover)
            startActivity(intent)
        }

        btnViewSolarData.setOnClickListener {
            val city = tvDefaultCity.text.toString()
            val intent = Intent(this, ThirdPage::class.java)
            intent.putExtra("city", city)
            intent.putExtra("SolarRadiation", currentSolarRadiation)
            intent.putExtra("ClearSkySolarRadiation", currentClearSkySolarRadiation)
            intent.putExtra("LongwaveRadiation", currentLongwaveRadiation)
            intent.putExtra("TopAtmosphereSolarRadiation", currentTopAtmosphereSolarRadiation)
            startActivity(intent)
        }
    }

    private fun createRetrofitClient(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = createRetrofitClient(WEATHER_BASE_URL)
        val apiService = retrofit.create(WeatherApiService::class.java)
        apiService.getWeather(city, WEATHER_API_KEY, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            currentTemperature = weatherData.main.temp.toDouble()
                            currentWindSpeed = weatherData.wind.speed
                            currentCloudCover = weatherData.clouds.all
                            Log.d("MainActivity", "Weather data fetched successfully for $city")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        tvDefaultCity.text = "Error fetching weather: $errorBody"
                        Log.e("MainActivity", "Error fetching weather data: $errorBody")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    tvDefaultCity.text = "Network error: ${t.message}"
                    Log.e("MainActivity", "Network error: ${t.message}")
                }
            })
    }
}
