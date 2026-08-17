package com.example.parkaifront.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkaifront.data.model.LoginRequest
import com.example.parkaifront.data.remote.RetrofitClient
import com.example.parkaifront.ui.theme.ParkaiBlue
import com.example.parkaifront.ui.theme.ParkaiBlueDark
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onBackClick: () -> Unit = {},
    onLoginSuccess: (String) -> Unit = {}
) {

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(
            modifier = Modifier.height(60.dp)
        )

        Text(
            text = "Iniciar sesión",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = ParkaiBlueDark
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Ingresá a tu cuenta de ParkAI",
            fontSize = 15.sp,
            color = Color(0xFF6B7280)
        )

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = null
            },
            label = {
                Text("Correo electrónico")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = null
            },
            label = {
                Text("Contraseña")
            },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        errorMessage?.let { message ->

            Text(
                text = message,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {

                if (email.isBlank() || password.isBlank()) {

                    errorMessage = "Completá todos los campos"

                    return@Button
                }

                isLoading = true
                errorMessage = null

                scope.launch {

                    try {

                        val response = RetrofitClient.authApi.login(
                            LoginRequest(
                                email = email.trim(),
                                password = password
                            )
                        )

                        if (response.isSuccessful) {

                            val authResponse = response.body()

                            if (authResponse != null) {

                                // Login correcto
                                onLoginSuccess(authResponse.token)

                            } else {

                                errorMessage =
                                    "El servidor no devolvió el token."

                            }

                        } else {

                            errorMessage = when (response.code()) {

                                401 ->
                                    "Correo o contraseña incorrectos."

                                404 ->
                                    "No existe una cuenta con ese correo."

                                else ->
                                    "Error al iniciar sesión. Código: ${response.code()}"
                            }
                        }

                    } catch (e: Exception) {

                        errorMessage =
                            "No se pudo conectar con el servidor."

                    } finally {

                        isLoading = false
                    }
                }
            },

            enabled = !isLoading,

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),

            shape = RoundedCornerShape(12.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = ParkaiBlue,
                contentColor = Color.White
            )
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

            } else {

                Text(
                    text = "Iniciar sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        TextButton(
            onClick = onBackClick
        ) {

            Text(
                text = "Volver",
                color = ParkaiBlueDark
            )
        }
    }
}