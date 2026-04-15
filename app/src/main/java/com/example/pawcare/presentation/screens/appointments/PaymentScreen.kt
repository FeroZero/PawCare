package com.example.pawcare.presentation.screens.appointments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pawcare.presentation.home.PawBottomBar
import com.example.pawcare.ui.theme.Background
import com.example.pawcare.ui.theme.TextPrimary
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPets: () -> Unit,
    onNavigateToProduct: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToPaymentList: () -> Unit,
    onNavigateToConfirmation: (String, Double, String, String, String, String, String) -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            if (effect is PaymentSideEffect.NavigateToConfirmation) {
                onNavigateToConfirmation(
                    effect.receiptNumber,
                    effect.amount,
                    effect.petName,
                    effect.service,
                    effect.date,
                    effect.method,
                    effect.employee
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Realizar Cobro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
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
        bottomBar = {
            PawBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToPets = onNavigateToPets,
                onNavigateToProduct = onNavigateToProduct,
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToPaymentList = onNavigateToPaymentList,
                currentRoute = ""
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // RESUMEN DE LA CITA
            AppointmentSummaryCard(state)

            Spacer(modifier = Modifier.height(24.dp))

            Text("MÉTODO DE PAGO", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PaymentMethodCard(
                    label = "Tarjeta",
                    icon = Icons.Default.CreditCard,
                    isSelected = state.paymentMethod == PaymentMethod.CARD,
                    onClick = { viewModel.onEvent(PaymentUiEvent.OnMethodSelected(PaymentMethod.CARD)) },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodCard(
                    label = "Efectivo",
                    icon = Icons.Default.Payments,
                    isSelected = state.paymentMethod == PaymentMethod.CASH,
                    onClick = { viewModel.onEvent(PaymentUiEvent.OnMethodSelected(PaymentMethod.CASH)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.paymentMethod == PaymentMethod.CARD) {
                CardDetailsSection(state, viewModel)
            } else if (state.paymentMethod == PaymentMethod.CASH) {
                CashDetailsSection(state, viewModel)
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.onEvent(PaymentUiEvent.OnConfirmPayment) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D2314)),
                enabled = !state.isLoading && state.paymentMethod != null
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Confirmar Cobro — RD$${state.amount}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AppointmentSummaryCard(state: PaymentUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF3D2314))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Pets, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(state.petName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(state.serviceName, color = Color.White.copy(alpha = 0.65f), fontSize = 9.sp)
                    }
                }
                Text(
                    "RD$${state.amount}",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow(Icons.Default.CalendarMonth, state.date)
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow(Icons.Default.PersonOutline, state.employeeName)
            }
        }
    }
}

@Composable
fun SummaryRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
    }
}

@Composable
fun PaymentMethodCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFDF9F5) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.5.dp,
            color = if (isSelected) Color(0xFF3D2314) else Color(0xFFE2D9CF)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon, null,
                tint = if (isSelected) Color(0xFF3D2314) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                label,
                color = if (isSelected) Color(0xFF3D2314) else Color.Gray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .border(
                        1.dp,
                        if (isSelected) Color(0xFF3D2314) else Color(0xFFE2D9CF),
                        CircleShape
                    )
                    .background(if (isSelected) Color(0xFF3D2314) else Color.Transparent)
            )
        }
    }
}

@Composable
fun CardDetailsSection(state: PaymentUiState, viewModel: PaymentViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            PaymentTextField(
                value = state.cardNumber,
                onValueChange = { viewModel.onEvent(PaymentUiEvent.OnCardNumberChanged(it)) },
                placeholder = "•••• •••• •••• XXXX",
                icon = Icons.Default.Memory
            )
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
            Row {
                PaymentTextField(
                    value = state.expiration,
                    onValueChange = { viewModel.onEvent(PaymentUiEvent.OnExpirationChanged(it)) },
                    placeholder = "MM/AA",
                    modifier = Modifier.weight(1f)
                )
                Box(modifier = Modifier.width(0.5.dp).height(56.dp).background(Color.LightGray.copy(alpha = 0.5f)))
                PaymentTextField(
                    value = state.cvv,
                    onValueChange = { viewModel.onEvent(PaymentUiEvent.OnCvvChanged(it)) },
                    placeholder = "CVV",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CashDetailsSection(state: PaymentUiState, viewModel: PaymentViewModel) {
    OutlinedTextField(
        value = state.cashReceived,
        onValueChange = { viewModel.onEvent(PaymentUiEvent.OnCashChanged(it)) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Monto recibido (opcional)", color = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE2D9CF),
            focusedBorderColor = Color(0xFF3D2314)
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
        )
    )
}

@Composable
fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = icon?.let { { Icon(it, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) } },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}
