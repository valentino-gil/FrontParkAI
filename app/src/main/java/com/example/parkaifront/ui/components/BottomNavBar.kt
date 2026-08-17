package com.example.parkaifront.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkaifront.ui.theme.ParkaiBlue
import androidx.compose.foundation.background

enum class BottomNavItem(val label: String) {
    MAPA("Mapa"),
    FAVORITOS("Favoritos"),
    REPORTAR("Reportar"),
    HISTORIAL("Historial"),
    PERFIL("Perfil")
}

@Composable
fun BottomNavBar(
    selectedItem: BottomNavItem,
    onItemSelected: (BottomNavItem) -> Unit
) {
    Surface(
        color = Color.White,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                icon = if (selectedItem == BottomNavItem.MAPA) Icons.Filled.Map else Icons.Outlined.Map,
                item = BottomNavItem.MAPA,
                selected = selectedItem == BottomNavItem.MAPA,
                onClick = onItemSelected
            )
            NavBarItem(
                icon = if (selectedItem == BottomNavItem.FAVORITOS) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                item = BottomNavItem.FAVORITOS,
                selected = selectedItem == BottomNavItem.FAVORITOS,
                onClick = onItemSelected
            )

            // Botón central destacado
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onItemSelected(BottomNavItem.REPORTAR) }
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(ParkaiBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Reportar",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reportar",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = ParkaiBlue
                )
            }

            NavBarItem(
                icon = if (selectedItem == BottomNavItem.HISTORIAL) Icons.Filled.History else Icons.Outlined.History,
                item = BottomNavItem.HISTORIAL,
                selected = selectedItem == BottomNavItem.HISTORIAL,
                onClick = onItemSelected
            )
            NavBarItem(
                icon = if (selectedItem == BottomNavItem.PERFIL) Icons.Filled.Person else Icons.Outlined.Person,
                item = BottomNavItem.PERFIL,
                selected = selectedItem == BottomNavItem.PERFIL,
                onClick = onItemSelected
            )
        }
    }
}

@Composable
private fun NavBarItem(
    icon: ImageVector,
    item: BottomNavItem,
    selected: Boolean,
    onClick: (BottomNavItem) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick(item) }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = item.label,
            tint = if (selected) ParkaiBlue else Color(0xFF9CA3AF),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) ParkaiBlue else Color(0xFF9CA3AF)
        )
    }
}