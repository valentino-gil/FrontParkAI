package com.example.parkaifront

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.parkaifront.ui.screens.RegisterScreen
import com.example.parkaifront.ui.screens.VerifyCodeScreen
import com.example.parkaifront.ui.screens.WelcomeScreen
import kotlinx.coroutines.delay
import com.example.parkaifront.ui.screens.SuccessScreen
import com.example.parkaifront.ui.screens.OnboardingScreen
import com.example.parkaifront.ui.screens.Onboarding2Screen
import com.example.parkaifront.ui.screens.Onboarding3Screen

// Rutas de navegación
object Routes {
    const val WELCOME = "welcome"
    const val REGISTER = "register"
    const val VERIFY_CODE = "verify_code/{email}"
    const val VERIFY_SUCCESS = "verify_success"
    const val ONBOARDING = "onboarding"
    const val ONBOARDING_2 = "onboarding_2"
    const val ONBOARDING_3 = "onboarding_3"
    // const val LOGIN = "login" // cuando la crees

    fun verifyCode(email: String) = "verify_code/$email"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ParkaiApp()
            }
        }
    }
}

@Composable
fun ParkaiApp() {
    // Controla si ya terminó el splash
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // duración del splash en milisegundos (2 segundos)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        AppNavHost()
    }
}

@Composable
fun AppNavHost() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onGoogleSignIn = { /* TODO */ },
                onAppleSignIn = { /* TODO */ },
                onEmailSignIn = { /* TODO: navegar a login */ },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.popBackStack() // o navegar a la pantalla de login
                },
                onRegisterSuccess = { nombre, email, password ->
                    // El registro ya se hizo dentro de RegisterScreen (llamada a la API).
                    // Acá solo navegamos a la pantalla de verificación de código.
                    navController.navigate(Routes.verifyCode(email))
                }
            )
        }

        composable(Routes.VERIFY_CODE) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            VerifyCodeScreen(
                email = email,
                onBackClick = {
                    navController.popBackStack()
                },
                onVerifySuccess = { token ->
                    // TODO: guardar el token (DataStore/SharedPreferences) y navegar al home
                    // navController.navigate(Routes.HOME) {
                    //     popUpTo(Routes.WELCOME) { inclusive = true }
                    // }
                }
            )
        }

        composable(Routes.VERIFY_CODE) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            VerifyCodeScreen(
                email = email,
                onBackClick = {
                    navController.popBackStack()
                },
                onVerifySuccess = { token ->
                    // TODO: acá guardá el token (DataStore/SharedPreferences) si lo necesitás
                    navController.navigate(Routes.VERIFY_SUCCESS) {
                        // saca VERIFY_CODE del stack para que no se pueda volver con "atrás"
                        popUpTo(Routes.VERIFY_CODE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.VERIFY_SUCCESS) {
            SuccessScreen(
                onAcceptClick = {
                    navController.navigate(Routes.ONBOARDING) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onSkipClick = {
                    // TODO: navegar directo al login o al home
                },
                onNextClick = {
                    navController.navigate(Routes.ONBOARDING_2)
                }
            )
        }

        composable(Routes.ONBOARDING_2) {
            Onboarding2Screen(
                onSkipClick = {
                    // TODO: navegar directo al login o al home
                },
                onNextClick = {
                    navController.navigate(Routes.ONBOARDING_3)
                }
            )
        }

        composable(Routes.ONBOARDING_3) {
            Onboarding3Screen(
                onSkipClick = {
                    // TODO: navegar directo al login o al home
                },
                onNextClick = {
                    // TODO: ¿es la última? navegar al login/home
                }
            )
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.parkaisplash),
            contentDescription = "ParkAI",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}