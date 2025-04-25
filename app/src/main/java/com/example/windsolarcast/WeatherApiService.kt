package com.example.windsolarcast

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherResponse>

    // Add Geocoding endpoint
    @GET("geo/1.0/direct") // Endpoint for geocoding city name to coordinates
    fun getCoordinates(
        @Query("q") city: String,
        @Query("limit") limit: Int = 1, // Limit to 1 result
        @Query("appid") apiKey: String
    ): Call<List<GeocodingResponse>> // The response is a list of locations
}
