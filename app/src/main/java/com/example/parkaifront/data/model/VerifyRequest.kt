package com.example.parkaifront.data.model

data class VerifyRequest(
    val email: String,
    val code: String
)