package com.example.pawcare.presentation.screens.appointments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Visibility
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
import com.example.pawcare.domain.model.Payment
import com.example.pawcare.presentation.home.PawBottomBar
import com.example.pawcare.ui.theme.Background
import com.example.pawcare.ui.theme.TextPrimary

@Composable
fun PaymentListScreen(
    state: PaymentListUiState,
    onEvent: (PaymentListUiEvent) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPets: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToPaymentList: () -> Unit // Parámetro añadido
) {
    var paymentToDelete by remember { mutableStateOf<Payment?>(null) }

    if (paymentToDelete != null) {
        AlertDialog(
            onDismissRequest = { paymentToDelete = null },
            title = { Text("¿Eliminar cobro?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer. ¿Deseas eliminar el cobro de RD$${paymentToDelete?.amount} de ${paymentToDelete?.id?.take(5)}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        paymentToDelete?.let { onEvent(PaymentListUiEvent.OnDeletePayment(it.id)) }
                        paymentToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { paymentToDelete = null }) {
                    Text("Cancelar", color = Color(0xFF3D2314))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        bottomBar = {
            PawBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToPets = onNavigateToPets,
                onNavigateToProduct = onNavigateToProducts,
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToPaymentList = onNavigateToPaymentList, // Pasar al componente
                currentRoute = "payment_list"
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Mis Cobros",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D2314)
            )

            // FILTROS
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { 
                    PaymentFilterChip("Todos", state.totalCount, state.selectedFilter == PaymentFilter.ALL) {
                        onEvent(PaymentListUiEvent.OnFilterSelected(PaymentFilter.ALL))
                    }
                }
                item { 
                    PaymentFilterChip("Tarjeta", state.cardCount, state.selectedFilter == PaymentFilter.CARD) {
                        onEvent(PaymentListUiEvent.OnFilterSelected(PaymentFilter.CARD))
                    }
                }
                item { 
                    PaymentFilterChip("Efectivo", state.cashCount, state.selectedFilter == PaymentFilter.CASH) {
                        onEvent(PaymentListUiEvent.OnFilterSelected(PaymentFilter.CASH))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.filteredPayments.isEmpty() && !state.isLoading) {
                EmptyPaymentsView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.filteredPayments) { payment ->
                        PaymentCard(
                            payment = payment,
                            onViewDetail = { onNavigateToDetail(payment.id) },
                            onDelete = { paymentToDelete = payment }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentFilterChip(label: String, count: Int, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text("$label ($count)") },
        shape = RoundedCornerShape(20.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF3D2314),
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = Color.Gray
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) Color.Transparent else Color(0xFFE2D9CF)
        )
    )
}

@Composable
fun PaymentCard(payment: Payment, onViewDetail: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0E8DF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Pets, null, tint = Color(0xFF3D2314), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Mascota", fontSize = 12.sp, fontWeight = FontWeight.W500)
                        Text("Servicio", fontSize = 9.sp, color = Color.Gray)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("RD$${payment.amount}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(payment.paymentDate.take(10), fontSize = 8.sp, color = Color.Gray)
                }
            }

            // Badge Método
            val methodColors = if (payment.paymentMethod.lowercase() == "tarjeta") {
                Color(0xFFDBEAFE) to Color(0xFF1E40AF)
            } else {
                Color(0xFFD1FAE5) to Color(0xFF065F46)
            }
            
            Box(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)) {
                Surface(color = methodColors.first, shape = RoundedCornerShape(20.dp)) {
                    Text(
                        text = payment.paymentMethod,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        color = methodColors.second,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0ECE7))

            Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                TextButton(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(Icons.Outlined.Visibility, null, tint = Color(0xFF3D2314), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver detalle", color = Color(0xFF3D2314), fontSize = 12.sp)
                }
                Box(modifier = Modifier.width(0.5.dp).fillMaxHeight().background(Color(0xFFF0ECE7)))
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFB91C1C), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar", color = Color(0xFFB91C1C), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyPaymentsView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.AutoMirrored.Outlined.ReceiptLong, null, modifier = Modifier.size(80.dp), tint = Color(0xFFC8BFB5))
        Spacer(modifier = Modifier.height(24.dp))
        Text("No hay cobros registrados", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
        Text("Los cobros aparecerán aquí al procesar pagos", color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
    }
}
