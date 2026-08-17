package com.example.parkaifront.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkaifront.data.model.CreateParkingReportRequest
import com.example.parkaifront.data.remote.RetrofitClient
import com.example.parkaifront.ui.theme.ParkaiBlue
import com.example.parkaifront.ui.theme.ParkaiBlueDark
import kotlinx.coroutines.launch

// TODO: reemplazar por los valores reales del enum ReportType del backend
enum class AvailabilityOption(
    val label: String,
    val description: String,
    val emoji: String,
    val color: Color,
    val reportType: String
) {
    MANY(
        "Hay varios lugares",
        "Es fácil encontrar lugar para estacionar.",
        "",
        Color(0xFF22C55E),
        "FOUND"
    ),
    FEW(
        "Hay pocos lugares",
        "Cuesta encontrar lugar.",
        "",
        Color(0xFFF59E0B),
        "FOUND" // decisión: "cuesta" pero todavía se encuentra. Cambiá a "NOT_FOUND" si preferís lo contrario
    ),
    FULL(
        "Está casi lleno",
        "Muy difícil encontrar lugar.",
        "",
        Color(0xFFEF4444),
        "NOT_FOUND"
    )
}

@Composable
fun ReportScreen(
    streetName: String,
    latitude: Double,
    longitude: Double,
    authToken: String,
    onClose: () -> Unit = {},
    onReportSent: () -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf<AvailabilityOption?>(null) }
    var comment by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    fun sendReport() {
        val option = selectedOption ?: return
        errorMessage = null
        isLoading = true

        scope.launch {
            try {
                val response = RetrofitClient.parkingReportApi.createReport(
                    token = "Bearer $authToken",
                    request = CreateParkingReportRequest(
                        streetName = streetName,
                        latitude = latitude,
                        longitude = longitude,
                        reportType = option.reportType
                    )
                )

                if (response.isSuccessful) {
                    onReportSent()
                } else {
                    errorMessage = "No se pudo enviar el reporte (${response.code()})."
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.javaClass.simpleName} - ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
                Text(
                    text = "Reportar disponibilidad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParkaiBlueDark,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tu reporte ayuda a toda la comunidad\na encontrar lugar más fácil.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card con la ubicación seleccionada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "Ubicación seleccionada",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = streetName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ParkaiBlueDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿Cómo está el estacionamiento acá?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ParkaiBlueDark
            )
            Text(
                text = "Seleccioná la opción que mejor represente la situación actual.",
                fontSize = 13.sp,
                color = Color(0xFF6B7280)
            )

            Spacer(modifier = Modifier.height(12.dp))

            AvailabilityOption.entries.forEach { option ->
                val isSelected = selectedOption == option
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) ParkaiBlue else Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedOption = option }
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(option.color.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(option.emoji, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(option.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(option.description, fontSize = 12.sp, color = Color(0xFF6B7280))
                    }
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedOption = option },
                        colors = RadioButtonDefaults.colors(selectedColor = ParkaiBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { if (it.length <= 120) comment = it },
                placeholder = { Text("Agregá un comentario (opcional)") },
                supportingText = { Text("${comment.length}/120", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End) },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Icon(Icons.Outlined.Shield, contentDescription = null, tint = ParkaiBlue)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Tu reporte es anónimo", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ParkaiBlueDark)
                    Text("No se publica tu nombre ni datos personales.", fontSize = 11.sp, color = Color(0xFF6B7280))
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { sendReport() },
                enabled = selectedOption != null && !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ParkaiBlue),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviar reporte", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}