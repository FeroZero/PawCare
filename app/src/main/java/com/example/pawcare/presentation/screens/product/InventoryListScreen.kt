package com.example.pawcare.presentation.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pawcare.domain.model.Product
import com.example.pawcare.presentation.components.products.ProductUiEvent
import com.example.pawcare.presentation.components.products.ProductUiState
import com.example.pawcare.presentation.home.PawBottomBar
import com.example.pawcare.presentation.screens.pet.PawCareCard
import com.example.pawcare.ui.theme.*

@Composable
fun InventoryListScreen(
    state: ProductUiState,
    onEvent: (ProductUiEvent) -> Unit,
    onNavigateToForm: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToPets: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToPaymentList: () -> Unit
) {
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("¿Eliminar producto?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción no se puede deshacer. ¿Estás seguro de que quieres eliminar '${productToDelete?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        productToDelete?.let { onEvent(ProductUiEvent.OnDeleteProduct(it.id)) }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar")
                }
            },
            containerColor = Surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(ProductUiEvent.OnClearForm)
                    onNavigateToForm()
                },
                containerColor = Accent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        },
        bottomBar = {
            PawBottomBar(
                onNavigateToHome = onNavigateToHome,
                onNavigateToPets = onNavigateToPets,
                onNavigateToProduct = { /* Ya estamos aquí */ },
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToPaymentList = onNavigateToPaymentList,
                currentRoute = "product_list"
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
            Text(
                text = "Gestión de Inventario",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(ProductUiEvent.OnSearchQueryChange(it)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por nombre o categoría...", color = MutedText) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MutedText) },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(state.products) { product ->
                    InventoryItem(
                        product = product,
                        onEdit = {
                            onEvent(ProductUiEvent.OnEditProductClick(product))
                            onNavigateToForm()
                        },
                        onDelete = {
                            productToDelete = product
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryItem(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    PawCareCard {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Categoría: ${product.category}", style = MaterialTheme.typography.bodySmall, color = MutedText)
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Text("Stock: ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = product.stock.toString(),
                        color = if (product.stock < 5) Color.Red else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Precio: $${product.price}", fontWeight = FontWeight.Bold, color = Accent)
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Gold)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.7f))
                }
            }
        }
    }
}
