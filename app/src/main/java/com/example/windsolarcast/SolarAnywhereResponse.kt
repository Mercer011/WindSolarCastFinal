package com.example.windsolarcast

data class SolarAnywhereResponse(
    val data: SolarData
)

data class SolarData(
    val ghi: Double, // Global Horizontal Irradiance (W/m²)
    val dni: Double, // Direct Normal Irradiance (W/m²)
    val wind_speed: Double // Wind speed in m/s
)
