package com.example.windsolarcast

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NasaPowerApiService {
    @Headers("User-Agent: MyAndroidApp/1.0") // NASA API requires this header
    @GET("api/temporal/daily/point")
    fun getSolarData(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("parameters") parameters: String = "ALLSKY_SFC_SW_DWN,CLRSKY_SFC_SW_DWN,ALLSKY_SFC_LW_DWN,ALLSKY_TOA_SW_DWN",
        @Query("community") community: String = "SB",
        @Query("format") format: String = "JSON"
    ): Call<NasaPowerResponse>
}

