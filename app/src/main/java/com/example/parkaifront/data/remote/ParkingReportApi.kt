package com.example.parkaifront.data.remote

import com.example.parkaifront.data.model.CreateParkingReportRequest
import com.example.parkaifront.data.model.ParkingReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ParkingReportApi {
    @POST("api/reports")
    suspend fun createReport(
        @Header("Authorization") token: String,
        @Body request: CreateParkingReportRequest
    ): Response<ParkingReportResponse>
}