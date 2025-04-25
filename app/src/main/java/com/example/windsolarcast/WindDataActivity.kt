package com.example.windsolarcast

import android.os.Bundle
import android.util.Log
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
import java.util.concurrent.TimeUnit // Import for connection timeouts


// Renamed from ThirdPage based on your MainActivity code mapping
class WindDataActivity : AppCompatActivity() {

    private lateinit var tvCity: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var ivTemperatureImage: ImageView
    private lateinit var tvWindSpeed: TextView
    private lateinit var ivWindSpeedImage: ImageView
    private lateinit var tvCloudCover: TextView
    private lateinit var ivCloudCoverImage: ImageView

    private val WEATHER_API_KEY = "315f293daca93eaf2847d326cec66326" // Should match MainActivity
    private val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/" // Should match MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wind_data)

        // Initialize TextViews and ImageViews
        tvCity = findViewById(R.id.tvCity)
        tvTemperature = findViewById(R.id.tvTemperature)
        ivTemperatureImage = findViewById(R.id.ivTemperatureImage)
        tvWindSpeed = findViewById(R.id.tvWindSpeed)
        ivWindSpeedImage = findViewById(R.id.ivWindSpeedImage)
        tvCloudCover = findViewById(R.id.tvCloudCover)
        ivCloudCoverImage = findViewById(R.id.ivCloudCoverImage)

        // Get data from the intent
        val city = intent.getStringExtra("city") ?: "Unknown City"
        val temperature = intent.getDoubleExtra("temperature", Double.NaN) // Use Double.NaN for check
        val windSpeed = intent.getFloatExtra("windSpeed", Float.NaN) // Use Float.NaN for check
        val cloudCover = intent.getIntExtra("cloudCover", -1) // Use -1 for check

        Log.d("WindDataActivity", "Received City: $city")
        Log.d("WindDataActivity", "Received Temperature: $temperature")
        Log.d("WindDataActivity", "Received Wind Speed: $windSpeed")
        Log.d("WindDataActivity", "Received Cloud Cover: $cloudCover")


        tvCity.text = "Wind Data for $city"

        // Check if data was passed via intent. If yes, display it. If not, fetch it.
        if (!temperature.isNaN() && !windSpeed.isNaN() && cloudCover != -1) {
            Log.d("WindDataActivity", "Displaying data from Intent extras.")
            displayWeatherData(temperature, windSpeed, cloudCover)
        } else {
            Log.d("WindDataActivity", "Data not in Intent extras. Fetching data for $city.")
            // Fetch weather data for the city
            fetchWeatherData(city)
        }
    }

    private fun createRetrofitClient(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY) // Log request and response bodies
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            // Add timeouts to prevent hanging
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
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

        Log.d("WindDataActivity", "Fetching weather data for city: $city") // Log the city being fetched

        apiService.getWeather(city, WEATHER_API_KEY, "metric")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        if (weatherData != null) {
                            val temperature = weatherData.main.temp
                            val windSpeed = weatherData.wind.speed
                            val cloudCover = weatherData.clouds.all

                            Log.d("WindDataActivity", "Weather data fetched successfully: Temp=$temperature, Wind=$windSpeed, Clouds=$cloudCover")
                            displayWeatherData(temperature.toDouble(), windSpeed, cloudCover) // Call display function
                        } else {
                            tvTemperature.text = "Error: Weather data not available"
                            Log.e("WindDataActivity", "Weather data body is null")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        val errorMessage = "Error fetching weather: ${response.code()} - $errorBody"
                        tvTemperature.text = errorMessage
                        Log.e("WindDataActivity", errorMessage)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    val errorMessage = "Network error: ${t.message}"
                    tvTemperature.text = errorMessage
                    Log.e("WindDataActivity", errorMessage)
                }
            })
    }

    // New function to display the data and set images
    private fun displayWeatherData(temperature: Double, windSpeed: Float, cloudCover: Int) {
        tvTemperature.text = "Temperature: %.2f°C".format(temperature)
        tvWindSpeed.text = "Wind Speed: $windSpeed m/s"
        tvCloudCover.text = "Cloud Cover: $cloudCover%"

        // Set images based on temperature
        if (temperature > 30) {
            ivTemperatureImage.setImageResource(R.drawable.hot_temperature_image) // Make sure these drawables exist
        } else if (temperature < 10) {
            ivTemperatureImage.setImageResource(R.drawable.cold_temperature_image) // Make sure these drawables exist
        } else {
            ivTemperatureImage.setImageResource(R.drawable.default_temperature_image) // Make sure this drawable exists
        }

        // Set wind image based on speed
        if (windSpeed > 10) {
            ivWindSpeedImage.setImageResource(R.drawable.high_wind_power_image) // Make sure these drawables exist
        } else if (windSpeed > 5) {
            ivWindSpeedImage.setImageResource(R.drawable.moderate_wind_power_image) // Make sure these drawables exist
        } else {
            ivWindSpeedImage.setImageResource(R.drawable.low_wind_power_image) // Make sure these drawables exist
        }

        // Set cloud cover image based on coverage (Corrected image names assumed)
        if (cloudCover > 75) {
            ivCloudCoverImage.setImageResource(R.drawable.high_solar_power_image) // Replace with your actual drawable name
        } else if (cloudCover > 25) {
            ivCloudCoverImage.setImageResource(R.drawable.moderate_wind_power_image) // Replace with your actual drawable name
        } else {
            ivCloudCoverImage.setImageResource(R.drawable.low_wind_power_image








            ) // Replace with your actual drawable name
        }
    }
}
