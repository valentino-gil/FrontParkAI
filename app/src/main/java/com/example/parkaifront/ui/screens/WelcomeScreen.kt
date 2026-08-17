package com.example.parkaifront.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.offset
import com.example.parkaifront.ui.theme.ParkaiBlue
import com.example.parkaifront.ui.theme.ParkaiBlueDark
import com.example.parkaifront.ui.theme.ParkaiCyan
import com.example.parkaifront.ui.theme.ParkaiGray

@Composable
fun WelcomeScreen(
    onGoogleSignIn: () -> Unit = {},
    onAppleSignIn: () -> Unit = {},
    onEmailSignIn: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo (pin + auto)
            ParkaiLogo()

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre de marca
            ParkaiBrandName()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "— Estacioná mejor. Viví la ciudad. —",
                fontSize = 12.sp,
                color = ParkaiGray
            )

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "¡Bienvenido!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = ParkaiBlueDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Encontrá zonas con mayor disponibilidad de estacionamiento y ahorrá tiempo en cada viaje.",
                fontSize = 15.sp,
                color = Color(0xFF4B5563),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Google
            SocialButton(
                text = "Ingresar con Google",
                backgroundColor = ParkaiBlue,
                onClick = onGoogleSignIn,
                icon = { GoogleIcon() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botón Apple
            SocialButton(
                text = "Ingresar con Apple ID",
                backgroundColor = ParkaiBlue,
                onClick = onAppleSignIn,
                icon = { AppleIcon() }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botón Correo (con borde oscuro)
            Button(
                onClick = onEmailSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParkaiBlue,
                    contentColor = Color.White
                ),
                border = BorderStroke(1.5.dp, ParkaiBlueDark)
            ) {
                Text(
                    text = "Ingresar con correo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Registrate
            val annotatedText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = ParkaiGray)) {
                    append("¿No tenes una cuenta? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = ParkaiBlueDark,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("Registrate")
                }
            }
            Text(
                text = annotatedText,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onRegisterClick() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SocialButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ---------- Logo dibujado con Canvas (pin + auto + estelas) ----------
@Composable
private fun ParkaiLogo() {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Pin (gota) con gradiente
            val pinPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(w * 0.5f, h * 0.98f) // punta inferior
                cubicTo(
                    w * 0.15f, h * 0.65f,
                    w * 0.1f, h * 0.35f,
                    w * 0.5f, h * 0.05f
                )
                cubicTo(
                    w * 0.9f, h * 0.35f,
                    w * 0.85f, h * 0.65f,
                    w * 0.5f, h * 0.98f
                )
                close()
            }
            drawPath(
                path = pinPath,
                brush = Brush.linearGradient(
                    colors = listOf(ParkaiCyan, ParkaiBlue, ParkaiBlueDark),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Círculo blanco interior
            drawCircle(
                color = Color.White,
                radius = w * 0.24f,
                center = Offset(w * 0.5f, h * 0.38f)
            )

            // Sombra elipse abajo
            drawOval(
                color = Color(0x22000000),
                topLeft = Offset(w * 0.28f, h * 0.97f),
                size = androidx.compose.ui.geometry.Size(w * 0.44f, h * 0.05f)
            )
        }

        // Ícono de auto centrado en el círculo blanco
        Icon(
            imageVector = Icons.Filled.DirectionsCar,
            contentDescription = "Auto",
            tint = ParkaiBlueDark,
            modifier = Modifier
                .size(34.dp)
                .offset(y = (-16).dp)
        )
    }
}

@Composable
private fun ParkaiBrandName() {
    val text = buildAnnotatedString {
        withStyle(style = SpanStyle(color = ParkaiBlueDark, fontWeight = FontWeight.ExtraBold)) {
            append("PARK")
        }
        withStyle(style = SpanStyle(color = ParkaiCyan, fontWeight = FontWeight.ExtraBold)) {
            append("AI")
        }
    }
    Text(text = text, fontSize = 34.sp, letterSpacing = 1.sp)
}

// ---------- Íconos simples de Google/Apple (placeholders vectoriales) ----------
@Composable
private fun GoogleIcon() {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            color = ParkaiBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun AppleIcon() {
    Text(
        text = "\uF8FF", // glifo de Apple (requiere fuente del sistema en iOS; en Android se recomienda un vector real)
        color = Color.White,
        fontSize = 18.sp
    )
}