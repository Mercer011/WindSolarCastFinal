package com.example.windsolarcast

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.ui.semantics.text
import androidx.core.graphics.values
//import androidx.privacysandbox.tools.core.generator.build
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
    private lateinit var tvDefaultCity: TextView

    private val weatherApiKey = "315f293daca93eaf2847d326cec66326" // Replace with your actual key
    private val weatherBaseUrl = "https://api.openweathermap.org/data/2.5/"
    private val nasaBaseUrl = "https://power.larc.nasa.gov/"

    private val defaultCity = "Jalandhar"
    private val defaultLat = 31.3260
    private val defaultLon = 75.5762

    private var currentCity: String = defaultCity
    private var temperature: Double = 0.0
    private var windSpeed: Float = 0.0f
    private var cloudCover: Int = 0
    private var ghi: Double = 0.0
    private var clearSkySolar: Double = 0.0
    // Add other relevant solar parameters here

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity = findViewById(R.id.etCity)
        btnSearch = findViewById(R.id.btnSearch)
        tvDefaultCity = findViewById(R.id.tvDefaultCity)

        etCity.setText(defaultCity)
        fetchWeatherData(defaultCity, defaultLat, defaultLon)
        tvDefaultCity.text = defaultCity

        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                currentCity = city
                fetchWeatherData(city, defaultLat, defaultLon)
                tvDefaultCity.text = city
            } else {
                tvDefaultCity.text = "Please enter a city name" // Or show a Toast
            }
        }
    }

    private fun createRetrofitClient(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun fetchWeatherData(city: String, lat: Double, lon: Double) {
        val retrofit = createRetrofitClient(weatherBaseUrl)
        val apiService = retrofit.create(WeatherApiService::class.java)
        apiService.getWeather(city, weatherApiKey, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            temperature = weatherData.main.temp.toDouble()
                            windSpeed = weatherData.wind.speed
                            cloudCover = weatherData.clouds.all
                            fetchSolarAndWindData(lat, lon, windSpeed, cloudCover)
                        } else {
                            tvDefaultCity.text = "Could not retrieve weather data"
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        tvDefaultCity.text = "Error fetching weather: $errorBody"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    tvDefaultCity.text = "Network error: ${t.message}"
                }
            })
    }

    private fun fetchSolarAndWindData(lat: Double, lon: Double, windSpeed: Float, cloudCover: Int) {
        val retrofit = createRetrofitClient(nasaBaseUrl)
        val apiService = retrofit.create(NasaPowerApiService::class.java)
        val startDate = "20240220" // Adjust date range as needed
        val endDate = "20240227"
        apiService.getSolarData(lat, lon, startDate, endDate)
            .enqueue(object : Callback<NasaPowerResponse> {
                override fun onResponse(
                    call: Call<NasaPowerResponse>,
                    response: Response<NasaPowerResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        val parameters = responseBody?.properties?.parameter
                        ghi = parameters?.ALLSKY_SFC_SW_DWN?.values?.lastOrNull() ?: 0.0
                        clearSkySolar = parameters?.CLRSKY_SFC_SW_DWN?.values?.lastOrNull() ?: 0.0
                        // Extract other solar parameters as needed

                        showDataChoiceDialog()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        tvDefaultCity.text = "Error fetching NASA data: $errorBody"
                    }
                }

                override fun onFailure(call: Call<NasaPowerResponse>, t: Throwable) {
                    tvDefaultCity.text = "Network error: ${t.message}"
                }
            })
    }

    private fun showDataChoiceDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Data Type")
            .setItems(arrayOf("Wind", "Solar")) { _, which ->
                when (which) {
                    0 -> navigateToWindData()
                    1 -> navigateToSolarData()
                }
            }
        builder.create().show()
    }

    private fun navigateToWindData() {
        val intent = Intent(this, WindDataActivity::class.java)
        intent.putExtra("city", currentCity)
        intent.putExtra("temperature", temperature)
        intent.putExtra("windSpeed", windSpeed)
        intent.putExtra("cloudCover", cloudCover)
        startActivity(intent)
    }

    private fun navigateToSolarData() {
        val intent = Intent(this, SolarDataActivity::class.java)
        intent.putExtra("city", currentCity)
        intent.putExtra("temperature", temperature)
        intent.putExtra("cloudCover", cloudCover)
        intent.putExtra("ghi", ghi)
        intent.putExtra("clearSkySolar", clearSkySolar)
        // Put other solar parameters as extras
        startActivity(intent)
    }
}