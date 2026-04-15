package com.example.pawcare.presentation.screens.appointments

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawcare.ui.theme.Background
import com.example.pawcare.ui.theme.TextPrimary

@Composable
fun AppointmentConfirmationScreen(
    date: String,
    time: String,
    petName: String,
    serviceName: String,
    onViewAppointments: () -> Unit,
    onScheduleAnother: () -> Unit,
    onGoToHome: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    BackHandler {
        onGoToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. ÍCONO DE ÉXITO
        AnimatedVisibility(
            visible = isVisible,
            enter = scaleIn(animationSpec = tween(600)) + fadeIn(animationSpec = tween(600))
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3D2314)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Éxito",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. MENSAJE PRINCIPAL
        Text(
            text = "¡Cita Agendada!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3D2314),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tu cita ha sido registrada exitosamente",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 3. CARD DE RESUMEN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                ConfirmationRow(icon = Icons.Default.Event, label = "Fecha", value = date)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                
                ConfirmationRow(icon = Icons.Default.AccessTime, label = "Hora", value = time)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                
                ConfirmationRow(icon = Icons.Default.Pets, label = "Mascota", value = petName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                
                ConfirmationRow(icon = Icons.Default.ContentCut, label = "Servicio", value = serviceName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                
                ConfirmationRow(icon = Icons.Default.Assignment, label = "Estado", value = "Pendiente", isStatus = true)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 4. BOTONES DE ACCIÓN
        Button(
            onClick = onViewAppointments,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2314))
        ) {
            Text("Ver mis citas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedButton(
            onClick = onScheduleAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFF3D2314))
        ) {
            Text("Agendar otra cita", fontWeight = FontWeight.Bold, color = Color(0xFF3D2314), fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = onGoToHome
        ) {
            Text("Ir al inicio", color = Color(0xFF3D2314), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ConfirmationRow(icon: ImageVector, label: String, value: String, isStatus: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isStatus) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isStatus) Color(0xFF3D2314) else TextPrimary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
