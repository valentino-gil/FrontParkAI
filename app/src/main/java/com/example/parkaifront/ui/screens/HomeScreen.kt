package com.example.parkaifront.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.example.parkaifront.ui.theme.ParkaiBlue
import com.example.parkaifront.ui.theme.ParkaiBlueDark
import kotlinx.coroutines.suspendCancellableCoroutine
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.util.SimpleInvalidationHandler
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.util.MapTileIndex
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Priority
import androidx.compose.foundation.layout.padding
import com.example.parkaifront.ui.components.BottomNavBar
import com.example.parkaifront.ui.components.BottomNavItem

private const val STADIA_API_KEY = "3ea2c03f-9dfb-4b4a-9b36-ccc76e24e502"

private val stadiaTileSource = object : OnlineTileSourceBase(
    "StadiaAlidade",
    0, 20, 256, ".png",
    arrayOf("https://tiles.stadiamaps.com/tiles/alidade_smooth/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val zoom = MapTileIndex.getZoom(pMapTileIndex)
        val x = MapTileIndex.getX(pMapTileIndex)
        val y = MapTileIndex.getY(pMapTileIndex)
        return "${baseUrl}$zoom/$x/$y.png?api_key=$STADIA_API_KEY"
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    userName: String = "Luis",
    onReportClick: () -> Unit = {}
) {

    val context = LocalContext.current
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var selectedNavItem by remember { mutableStateOf(BottomNavItem.MAPA) }

    val defaultLocation = GeoPoint(-34.5875, -58.4205)
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationPermission.status.isGranted, mapViewRef) {
        if (locationPermission.status.isGranted && mapViewRef != null) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                val currentLocationRequest = CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

                val location = fusedClient.getCurrentLocation(currentLocationRequest, null).await()
                location?.let {
                    mapViewRef?.controller?.animateTo(GeoPoint(it.latitude, it.longitude))
                }
            } catch (e: SecurityException) {
                // Permiso denegado en runtime, se queda con la ubicación default
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                selectedItem = selectedNavItem,
                onItemSelected = { item ->
                    selectedNavItem = item
                    if (item == BottomNavItem.REPORTAR) {
                        onReportClick()
                    }
                    // TODO: navegar a Favoritos, Historial, Perfil cuando existan esas pantallas
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(stadiaTileSource)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(defaultLocation)

                        if (locationPermission.status.isGranted) {
                            val locationOverlay =
                                MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                            locationOverlay.enableMyLocation()
                            overlays.add(locationOverlay)
                        }

                        mapViewRef = this
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hola, $userName 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParkaiBlueDark
                        )
                        Text(
                            text = "¿Dónde querés estacionar?",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE5E7EB), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = "",
                    onValueChange = { /* TODO: buscador de direcciones */ },
                    placeholder = { Text("Buscar dirección o lugar") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = onReportClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ParkaiBlue),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text("+ Reportar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

suspend fun com.google.android.gms.tasks.Task<android.location.Location>.await(): android.location.Location? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resumeWithException(it) }
    }