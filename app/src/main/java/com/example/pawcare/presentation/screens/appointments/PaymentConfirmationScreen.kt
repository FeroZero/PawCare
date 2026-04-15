package com.example.pawcare.presentation.screens.appointments

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun PaymentConfirmationScreen(
    receiptNumber: String,
    amount: Double,
    petName: String,
    service: String,
    date: String,
    method: String,
    employee: String,
    onDownloadReceipt: () -> Unit,
    onViewPaymentList: () -> Unit,
    onGoToHome: () -> Unit
) {
    BackHandler {
        onGoToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // HERO SUPERIOR
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0E8DF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF3D2314),
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Cobro exitoso!",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D2314)
            ),
            fontSize = 19.sp
        )
        
        Text(
            text = "El pago ha sido registrado correctamente",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // RECIBO CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // Header oscuro
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF3D2314))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recibo $receiptNumber",
                        color = Color.LightGray.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "RD$$amount",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Cuerpo blanco
                Column(modifier = Modifier.padding(20.dp)) {
                    ReceiptRow(label = "Mascota", value = petName)
                    ReceiptDivider()
                    ReceiptRow(label = "Servicio", value = service)
                    ReceiptDivider()
                    ReceiptRow(label = "Fecha", value = date)
                    ReceiptDivider()
                    ReceiptRow(label = "Método", value = method, isBadge = true)
                    ReceiptDivider()
                    ReceiptRow(label = "Atendido por", value = employee)
                    ReceiptDivider()
                    ReceiptRow(label = "Estado", value = "Pagado", isBadge = true, badgeColor = Color(0xFF4CAF50))
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // BOTONES
        Button(
            onClick = onDownloadReceipt,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2314))
        ) {
            Icon(Icons.Outlined.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Descargar recibo", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onViewPaymentList,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(13.dp),
            border = BorderStroke(1.dp, Color(0xFF3D2314))
        ) {
            Text("Ver lista de cobros", color = Color(0xFF3D2314), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onGoToHome) {
            Text("Ir al inicio", color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String, isBadge: Boolean = false, badgeColor: Color? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        
        if (isBadge) {
            val bgColor = badgeColor ?: if (value.lowercase() == "tarjeta") Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
            val textColor = if (badgeColor != null) Color.White else if (value.lowercase() == "tarjeta") Color(0xFF1976D2) else Color(0xFF388E3C)
            
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = value,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = textColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ReceiptDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 12.dp),
        thickness = 0.5.dp,
        color = Color(0xFFF5F0EA)
    )
}
