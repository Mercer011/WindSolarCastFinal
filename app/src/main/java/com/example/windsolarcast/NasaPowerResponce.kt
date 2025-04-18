package com.example.windsolarcast

data class NasaPowerResponse(
    val properties: Properties
)

data class Properties(
    val parameter: Parameter
)

data class Parameter(
    val ALLSKY_SFC_SW_DWN: Map<String, Double>,  // Solar energy reaching surface
    val CLRSKY_SFC_SW_DWN: Map<String, Double>,  // Solar energy under clear sky
    val ALLSKY_SFC_LW_DWN: Map<String, Double>,  // Longwave radiation from sky
    val ALLSKY_TOA_SW_DWN: Map<String, Double>   // Total solar radiation at the top of atmosphere
)
