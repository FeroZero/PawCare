package com.example.pawcare.presentation.screens.pet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.pawcare.domain.model.Pet
import com.example.pawcare.ui.theme.*

@Composable
fun PetListItem(
    pet: Pet,
    onClick: () -> Unit = {}
) {
    PawCareCard(
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar de la mascota (Círculo con borde Accent)
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = Background, // Color de fondo suave --bg
                border = BorderStroke(2.dp, Border) // Borde --border
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // Usamos icono Pets de Material Extended en lugar de emoji
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = Accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${pet.breed} · ${pet.age} años",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedText
                )
                // Información del dueño (Screen 03 del HTML)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Dueño: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedText
                    )
                    Text(
                        text = pet.ownerName,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Icono de navegación (Flecha)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ver perfil",
                tint = Border
            )
        }
    }
}