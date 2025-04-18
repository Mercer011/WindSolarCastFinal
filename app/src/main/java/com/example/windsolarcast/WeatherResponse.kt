package com.example.windsolarcast

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds
)

data class Weather(val description: String)
data class Main(val temp: Float)
data class Wind(val speed: Float)  // Wind speed in m/s
data class Clouds(val all: Int)   // Cloud cover percentage
