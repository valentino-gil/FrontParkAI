package com.example.parkaifront.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkaifront.ui.theme.ParkaiBlue
import com.example.parkaifront.ui.theme.ParkaiBlueDark
import com.example.parkaifront.ui.theme.ParkaiGray
import androidx.compose.material3.CircularProgressIndicator
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.parkaifront.data.model.RegisterRequest
import com.example.parkaifront.data.remote.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: (nombre: String, email: String, password: String) -> Unit = { _, _, _ -> }
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    var acceptTerms by remember { mutableStateOf(false) }
    var acceptEmails by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // El formulario es válido solo cuando todos los campos están completos,
    // las contraseñas coinciden y se aceptan los Términos y Condiciones.
    val isFormValid by remember {
        derivedStateOf {
            nombre.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    password == repeatPassword &&
                    acceptTerms
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = ParkaiBlue,
        unfocusedBorderColor = ParkaiGray,
        focusedLabelColor = ParkaiBlue,
        cursorColor = ParkaiBlue
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = ParkaiBlueDark
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Crear cuenta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParkaiBlueDark,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = ParkaiGray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = repeatPassword,
                    onValueChange = { repeatPassword = it },
                    label = { Text("Repetir contraseña") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = fieldColors,
                    isError = repeatPassword.isNotEmpty() && repeatPassword != password,
                    visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                            Icon(
                                imageVector = if (repeatPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (repeatPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                                tint = ParkaiGray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                if (repeatPassword.isNotEmpty() && repeatPassword != password) {
                    Text(
                        text = "Las contraseñas no coinciden",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = acceptTerms,
                        onCheckedChange = { acceptTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = ParkaiBlue)
                    )
                    Text(
                        text = "Acepto los Términos y Condiciones y la Política de Privacidad.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = acceptEmails,
                        onCheckedChange = { acceptEmails = it },
                        colors = CheckboxDefaults.colors(checkedColor = ParkaiBlue)
                    )
                    Text(
                        text = "Acepto recibir correos electronicos y/o SMS.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B5563),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                errorMessage?.let { message ->

                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = {

                        isLoading = true
                        errorMessage = null

                        scope.launch {

                            try {

                                val request = RegisterRequest(
                                    name = nombre,
                                    email = email,
                                    password = password
                                )

                                val response = RetrofitClient.authApi.register(request)

                                if (response.isSuccessful) {

                                    val user = response.body()

                                    if (user != null) {
                                        onRegisterSuccess(
                                            user.name,
                                            user.email,
                                            password
                                        )
                                    }

                                } else {

                                    errorMessage = when (response.code()) {
                                        400 -> "Los datos ingresados no son válidos."
                                        409 -> "El correo electrónico ya está registrado."
                                        else -> "No se pudo crear la cuenta."
                                    }
                                }

                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.javaClass.simpleName} - ${e.message}"
                            } finally {

                                isLoading = false
                            }
                        }
                    },
                    enabled = isFormValid && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkaiBlue,
                        contentColor = Color.White,
                        disabledContainerColor = ParkaiGray.copy(alpha = 0.5f),
                        disabledContentColor = Color.White
                    ),
                    border = if (isFormValid) BorderStroke(1.5.dp, ParkaiBlueDark) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isLoading) {

                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )

                    } else {

                        Text(
                            "Crear cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿Ya tenés una cuenta? ",
                        fontSize = 14.sp,
                        color = ParkaiGray
                    )
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ParkaiBlueDark,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}