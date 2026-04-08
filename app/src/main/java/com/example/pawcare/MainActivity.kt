package com.example.pawcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.pawcare.domain.repository.PetRepository
import com.example.pawcare.presentation.navigation.NavGraph
import com.example.pawcare.ui.theme.PawCareTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var petRepository: PetRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PawCareTheme {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    petRepository = petRepository
                )
            }
        }
    }
}
