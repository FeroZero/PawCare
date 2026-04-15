package com.example.pawcare.presentation.screens.pet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.presentation.components.pets.PetUiEvent
import com.example.pawcare.presentation.components.pets.PetUiState
import com.example.pawcare.ui.theme.*

@Composable
fun PetListScreen(
    state: PetUiState,
    onEvent: (PetUiEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = TextPrimary
                )
            }

            Text(
                text = "Mis Mascotas",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .background(Accent, RoundedCornerShape(10.dp))
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar", tint = Color.White)
            }
        }

        // BUSCADOR
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { onEvent(PetUiEvent.OnSearchQueryChange(it)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Buscar mascota...", color = MutedText) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MutedText) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Surface,
                unfocusedContainerColor = Surface,
                focusedBorderColor = Border,
                unfocusedBorderColor = Border
            )
        )

        // ESTADÍSTICAS
        Row(
            modifier = Modifier.padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatBox(state.totalRegistered.toString(), "Registradas", Accent, Modifier.weight(1f))
            StatBox(state.appointmentsToday.toString(), "Citas hoy", Gold, Modifier.weight(1f))
            StatBox(state.newPetsCount.toString(), "Nuevas", Color(0xFF4A7A4A), Modifier.weight(1f))
        }

        Text(
            text = "Todas las mascotas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // LISTA
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(state.pets) { pet ->
                PetListItem(pet = pet, onClick = { onEvent(PetUiEvent.OnPetClick(pet.id)) })
            }
        }
    }
}

@Composable
fun StatBox(value: String, label: String, color: Color, modifier: Modifier) {
    PawCareCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            Text(label, color = MutedText, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetListScreenPreview() {
    PawCareTheme {
        val state = PetUiState(
            pets = emptyList(),
            totalRegistered = 0,
            appointmentsToday = 0,
            newPetsCount = 0,
            searchQuery = ""
        )
        PetListScreen(
            state = state,
            onEvent = {},
            onNavigateToRegister = {},
            onBack = {}
        )
    }
}