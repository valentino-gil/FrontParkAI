package com.example.parkaifront.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkaifront.data.model.VerifyRequest
import com.example.parkaifront.data.model.ResendCodeRequest
import com.example.parkaifront.data.remote.RetrofitClient
import com.example.parkaifront.ui.theme.ParkaiBlue
import com.example.parkaifront.ui.theme.ParkaiBlueDark
import com.example.parkaifront.ui.theme.ParkaiGray
import kotlinx.coroutines.launch

private const val CODE_LENGTH = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyCodeScreen(
    email: String,
    onBackClick: () -> Unit = {},
    onVerifySuccess: (token: String) -> Unit = { _ -> }
) {
    // Un valor de texto por cada casillero del código
    val codeDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(CODE_LENGTH) { FocusRequester() } }

    var isLoading by remember { mutableStateOf(false) }
    var isResending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resendMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val code by remember { derivedStateOf { codeDigits.joinToString("") } }
    val isCodeComplete by remember { derivedStateOf { code.length == CODE_LENGTH } }

    fun verifyCode() {
        errorMessage = null
        resendMessage = null
        isLoading = true

        scope.launch {
            try {
                val response = RetrofitClient.authApi.verify(
                    VerifyRequest(email = email, code = code)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        onVerifySuccess(body.token)
                    } else {
                        errorMessage = "No se pudo verificar la cuenta."
                    }
                } else {
                    errorMessage = when (response.code()) {
                        400 -> "Código incorrecto o expirado."
                        404 -> "No encontramos una cuenta con ese correo."
                        else -> "No se pudo verificar la cuenta."
                    }
                    // Si falla, limpiamos el código para que lo reingresen
                    for (i in codeDigits.indices) codeDigits[i] = ""
                    focusRequesters.getOrNull(0)?.requestFocus()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.javaClass.simpleName} - ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resendCode() {
        errorMessage = null
        resendMessage = null
        isResending = true

        scope.launch {
            try {
                val response = RetrofitClient.authApi.resendCode(
                    ResendCodeRequest(email = email)
                )

                resendMessage = if (response.isSuccessful) {
                    "Te reenviamos el código a tu correo."
                } else {
                    when (response.code()) {
                        429 -> "Esperá unos segundos antes de pedir otro código."
                        else -> "No se pudo reenviar el código."
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.javaClass.simpleName} - ${e.message}"
            } finally {
                isResending = false
            }
        }
    }

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Verificá tu correo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParkaiBlueDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Ingresá el código de 6 dígitos que\nenviamos a tu correo electrónico.",
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 0 until CODE_LENGTH) {
                        OutlinedTextField(
                            value = codeDigits[i],
                            onValueChange = { newValue ->
                                val digit = newValue.filter { it.isDigit() }.takeLast(1)
                                codeDigits[i] = digit
                                errorMessage = null

                                if (digit.isNotEmpty() && i < CODE_LENGTH - 1) {
                                    focusRequesters[i + 1].requestFocus()
                                }

                                // Si el código quedó completo, verificamos automáticamente
                                if (codeDigits.joinToString("").length == CODE_LENGTH && !isLoading) {
                                    verifyCode()
                                }
                            },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            ),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ParkaiBlue,
                                unfocusedBorderColor = ParkaiGray,
                                cursorColor = ParkaiBlue
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesters[i])
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "¿No recibiste el código? ",
                        fontSize = 14.sp,
                        color = ParkaiGray
                    )
                    if (isResending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = ParkaiBlue,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Reenviar código",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParkaiBlueDark,
                            modifier = Modifier.clickable { resendCode() }
                        )
                    }
                }

                resendMessage?.let { message ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = message,
                        color = ParkaiBlue,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }

                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { verifyCode() },
                    enabled = isCodeComplete && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParkaiBlue,
                        contentColor = Color.White,
                        disabledContainerColor = ParkaiGray.copy(alpha = 0.5f),
                        disabledContentColor = Color.White
                    ),
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
                            "Verificar cuenta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}