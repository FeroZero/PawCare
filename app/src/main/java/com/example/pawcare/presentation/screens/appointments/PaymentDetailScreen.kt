package com.example.pawcare.presentation.screens.appointments

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.pawcare.domain.model.Payment
import com.example.pawcare.domain.repository.PaymentRepository
import com.example.pawcare.domain.util.Resource
import com.example.pawcare.ui.theme.Background
import com.example.pawcare.ui.theme.TextPrimary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    paymentId: String,
    paymentRepository: PaymentRepository,
    onBack: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var payment by remember { mutableStateOf<Payment?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(paymentId) {
        val result = paymentRepository.getPayments().first()
        if (result is Resource.Success) {
            payment = result.data.find { it.id == paymentId }
        }
        isLoading = false
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar este cobro?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            paymentRepository.deletePayment(paymentId)
                            showDeleteDialog = false
                            onDeleteSuccess()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Sí, eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("No, mantener", color = Color(0xFF3D2314))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del Cobro", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3D2314))
            }
        } else if (payment != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Recibo ${payment!!.receiptNumber}".uppercase(),
                    color = Color.Gray,
                    fontSize = 9.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // CARD PRINCIPAL
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        // CABECERA DE LA CARD
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF7F2EC))
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total pagado", color = Color.Gray, fontSize = 9.sp)
                                Text(
                                    "RD$${payment!!.amount}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3D2314)
                                )
                            }
                            Surface(
                                color = Color(0xFFD1FAE5),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "Pagado",
                                    color = Color(0xFF065F46),
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // FILAS DE DETALLE
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            DetailRowItem(Icons.Default.Pets, "Mascota", payment!!.petName)
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F0EA))
                            DetailRowItem(Icons.Default.ContentCut, "Servicio", payment!!.serviceName)
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F0EA))
                            DetailRowItem(Icons.Default.CalendarMonth, "Fecha y hora", payment!!.appointmentDate)
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F0EA))
                            DetailRowItem(Icons.Default.PersonOutline, "Atendido por", payment!!.employeeName)
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F0EA))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconBox(Icons.Default.CreditCard)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Método de pago", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(100.dp))
                                Spacer(modifier = Modifier.weight(1f))
                                val isCard = payment!!.paymentMethod.lowercase() == "tarjeta"
                                Surface(
                                    color = if (isCard) Color(0xFFDBEAFE) else Color(0xFFD1FAE5),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isCard) "Tarjeta •••• 4821" else "Efectivo",
                                        color = if (isCard) Color(0xFF1E40AF) else Color(0xFF065F46),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F0EA))
                            DetailRowItem(Icons.Default.AccessTime, "Hora de registro", payment!!.paymentTime)
                        }

                        // FILA TOTAL
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total cobrado", color = Color.Gray, fontSize = 10.sp)
                            Text(
                                "RD$${payment!!.amount}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3D2314)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // BOTONES
                Button(
                    onClick = { generateAndSharePdf(context, payment!!) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2314))
                ) {
                    Icon(Icons.Outlined.FileDownload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Descargar recibo", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Icon(Icons.Outlined.Delete, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar cobro", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Cobro no encontrado", color = Color.Gray)
                LaunchedEffect(Unit) {
                    launch {
                        kotlinx.coroutines.delay(2000)
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRowItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconBox(icon)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.width(100.dp))
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun IconBox(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF7F2EC)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color(0xFF3D2314), modifier = Modifier.size(18.dp))
    }
}

private fun generateAndSharePdf(context: Context, payment: Payment) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint()

    paint.textSize = 16f
    paint.isFakeBoldText = true
    canvas.drawText("Recibo de Pago - PawCare", 20f, 40f, paint)

    paint.textSize = 12f
    paint.isFakeBoldText = false
    var y = 80f
    canvas.drawText("Recibo: ${payment.receiptNumber}", 20f, y, paint); y += 25f
    canvas.drawText("Mascota: ${payment.petName}", 20f, y, paint); y += 25f
    canvas.drawText("Servicio: ${payment.serviceName}", 20f, y, paint); y += 25f
    canvas.drawText("Monto: RD$${payment.amount}", 20f, y, paint); y += 25f
    canvas.drawText("Fecha: ${payment.paymentDate}", 20f, y, paint); y += 25f
    canvas.drawText("Atendido por: ${payment.employeeName}", 20f, y, paint); y += 25f
    canvas.drawText("Estado: ${payment.status}", 20f, y, paint)

    pdfDocument.finishPage(page)

    val file = File(context.cacheDir, "Recibo_${payment.receiptNumber}.pdf")
    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF Generado", Toast.LENGTH_SHORT).show()
        
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(intent, "Compartir Recibo"))
    } catch (e: Exception) {
        Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
    } finally {
        pdfDocument.close()
    }
}
