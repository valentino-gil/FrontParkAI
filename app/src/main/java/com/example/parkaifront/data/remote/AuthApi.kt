package com.example.parkaifront.data.remote

import com.example.parkaifront.data.model.AuthResponse
import com.example.parkaifront.data.model.RegisterRequest
import com.example.parkaifront.data.model.ResendCodeRequest
import com.example.parkaifront.data.model.UserResponse
import com.example.parkaifront.data.model.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<UserResponse>

    @POST("api/auth/verify")
    suspend fun verify(@Body request: VerifyRequest): Response<AuthResponse>

    @POST("api/auth/resend-code")
    suspend fun resendCode(@Body request: ResendCodeRequest): Response<Unit>
}