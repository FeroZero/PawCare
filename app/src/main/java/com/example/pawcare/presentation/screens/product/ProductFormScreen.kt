package com.example.pawcare.presentation.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pawcare.presentation.components.products.ProductUiEvent
import com.example.pawcare.presentation.components.products.ProductUiState
import com.example.pawcare.ui.theme.*

@Composable
fun ProductFormScreen(
    state: ProductUiState,
    onEvent: (ProductUiEvent) -> Unit,
    onBack: () -> Unit,
    isEdit: Boolean = false
) {

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextPrimary)
            }
            Text(
                text = if (isEdit) "Editar Producto" else "Nuevo Producto",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        // Icono decorativo de Inventario (Sustituye emoji)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = Accent.copy(alpha = 0.1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = Accent,
                    modifier = Modifier.padding(20.dp)
                )
            }
        }

        Text(
            text = "DETALLES DEL INVENTARIO",
            style = MaterialTheme.typography.labelSmall,
            color = Accent,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo: Nombre
        OutlinedTextField(
            value = state.name,
            onValueChange = { onEvent(ProductUiEvent.OnNameChange(it)) },
            label = { Text("Nombre del Producto") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent)
        )

        Spacer(Modifier.height(12.dp))

        // Campo: Categoría
        OutlinedTextField(
            value = state.category,
            onValueChange = { onEvent(ProductUiEvent.OnCategoryChange(it)) },
            label = { Text("Categoría (Ej: Higiene, Accesorios)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent)
        )

        Spacer(Modifier.height(12.dp))

        // Fila: Precio y Stock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.price,
                onValueChange = { onEvent(ProductUiEvent.OnPriceChange(it)) },
                label = { Text("Precio ($)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent)
            )

            OutlinedTextField(
                value = state.stock,
                onValueChange = { onEvent(ProductUiEvent.OnStockChange(it)) },
                label = { Text("Stock Inicial") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Botón Guardar
        Button(
            onClick = { onEvent(ProductUiEvent.OnSaveProductClick) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Accent),
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (isEdit) "Actualizar Inventario" else "Guardar en Inventario",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductFormScreenPreview() {
    PawCareTheme {
        ProductFormScreen(
            state = ProductUiState(
                name = "Collar de Cuero",
                category = "Accesorios",
                price = "450.0",
                stock = "10"
            ),
            onEvent = {},
            onBack = {}
        )
    }
}