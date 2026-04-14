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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pawcare.domain.model.Product
import com.example.pawcare.presentation.components.products.ProductUiEvent
import com.example.pawcare.presentation.components.products.ProductUiState
import com.example.pawcare.presentation.screens.pet.PawCareCard
import com.example.pawcare.ui.theme.Accent
import com.example.pawcare.ui.theme.Background
import com.example.pawcare.ui.theme.Gold
import com.example.pawcare.ui.theme.MutedText
import com.example.pawcare.ui.theme.PawCareTheme
import com.example.pawcare.ui.theme.TextPrimary

@Composable
fun InventoryListScreen(
    state: ProductUiState,
    onEvent: (ProductUiEvent) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Accent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
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
                        onEdit = { onNavigateToEdit(product.id) },
                        onDelete = { onEvent(ProductUiEvent.OnDeleteProduct(product.id)) }
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

@Preview(showBackground = true, name = "Gestión Inventario")
@Composable
private fun InventoryListScreenPreview() {
    PawCareTheme {
        val mockProducts = listOf(
            Product("1", "Collar Cuero Premium", "Accesorios", 450.0, null, 12),
            Product("2", "Shampoo Hidratante", "Higiene", 280.0, null, 3),
            Product("3", "Juguete Cuerda XL", "Juguetes", 150.0, null, 25),
            Product("4", "Cama Ortopédica", "Muebles", 1200.0, null, 5)
        )

        val state = ProductUiState(
             products = mockProducts,
            isLoading = false,
            searchQuery = ""
        )

        InventoryListScreen(
            state = state,
            onEvent = {},
            onNavigateToCreate = {},
            onNavigateToEdit = {id ->}
        )
    }
}