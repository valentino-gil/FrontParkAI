package com.example.parkaifront.data.model

data class CreateParkingReportRequest(
    val streetName: String,
    val latitude: Double,
    val longitude: Double,
    val reportType: String
)

data class ParkingReportResponse(
    val id: Long?,
    val streetName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val reportType: String?,
    val reportTime: String?
)