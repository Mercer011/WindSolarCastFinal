package com.example.windsolarcast

import com.google.gson.annotations.SerializedName

// Data class to parse the Geocoding API response
// The API returns a list of these objects
data class GeocodingResponse(
    @SerializedName("name") val name: String,
    @SerializedName("local_names") val localNames: Map<String, String>?, // Optional local names
    @SerializedName("lat") val lat: Double, // Latitude
    @SerializedName("lon") val lon: Double, // Longitude
    @SerializedName("country") val country: String,
    @SerializedName("state") val state: String? // State is optional
)
