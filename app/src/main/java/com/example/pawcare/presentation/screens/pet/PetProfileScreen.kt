package com.example.pawcare.presentation.screens.pet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pawcare.domain.model.Owner
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.ui.theme.*

@Composable
fun PetProfileScreen(
    pet: Pet,
    owner: Owner,
    onBack: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(com.example.pawcare.ui.theme.Background)) {

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar",
                tint = Accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = com.example.pawcare.ui.theme.Background,
                border = BorderStroke(2.dp, Accent)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Column(Modifier.padding(start = 16.dp)) {
                Text(text = pet.name, style = MaterialTheme.typography.headlineMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SuggestionChip(onClick = {}, label = { Text(pet.breed) })
                    SuggestionChip(onClick = {}, label = { Text("${pet.age} años") })
                }
            }
        }

        PawCareCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Accent)
                Column(Modifier.padding(start = 12.dp)) {
                    Text(text = owner.fullName, fontWeight = FontWeight.Bold)
                    Text(
                        text = owner.phone,
                        color = MutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = owner.email,
                        color = MutedText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tabs
        val tabs = listOf("Historial", "Servicios", "Productos")
        TabRow(
            selectedTabIndex = 0,
            containerColor = Color.Transparent,
            contentColor = Accent,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = index == 0,
                    onClick = { },
                    text = { Text(title) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PetProfileScreenPreview() {
    PawCareTheme {
        val mockPet = Pet(
            id = "1",
            name = "Rocky",
            breed = "Golden Retriever",
            age = 3,
            photoUrl = null,
            ownerId = "owner123",
            ownerName = "Carlos Méndez",
            createdAt = "2023-08-01"
        )

        val mockOwner = Owner(
            id = "owner123",
            fullName = "Carlos Méndez",
            phone = "+1 (849) 555-0192",
            email = "carlos.mendez@example.com",
            address = "Av. Central 456",
            isVip = true,
            createdAt = "2023-08-01",
            pets = listOf(mockPet)
        )

        PetProfileScreen(
            pet = mockPet,
            owner = mockOwner,
            onBack = {}
        )
    }
}