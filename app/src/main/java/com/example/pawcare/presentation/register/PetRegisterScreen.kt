package com.example.pawcare.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetRegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToConfirmation: (String) -> Unit,
    viewModel: PetRegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PetRegisterEffect.NavigateToConfirmation -> onNavigateToConfirmation(effect.petId)
                PetRegisterEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Registro", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF5EFE6)
                )
            )
        },
        containerColor = Color(0xFFF5EFE6)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar placeholder
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).height(4.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.weight(1f).height(4.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.weight(1f).height(4.dp).background(MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(2.dp)))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Photo Picker Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Foto", style = MaterialTheme.typography.labelSmall)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("DATOS DEL PERRO")
            Spacer(modifier = Modifier.height(12.dp))
            
            PawTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(PetRegisterEvent.OnNameChanged(it)) },
                placeholder = "Nombre del perro"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PawTextField(
                value = state.breed,
                onValueChange = { viewModel.onEvent(PetRegisterEvent.OnBreedChanged(it)) },
                placeholder = "Raza"
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PawTextField(
                    value = state.age,
                    onValueChange = { viewModel.onEvent(PetRegisterEvent.OnAgeChanged(it)) },
                    placeholder = "Edad",
                    modifier = Modifier.weight(1f)
                )
                PawTextField(
                    value = state.weight,
                    onValueChange = { viewModel.onEvent(PetRegisterEvent.OnWeightChanged(it)) },
                    placeholder = "Peso",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("DATOS DEL DUEÑO")
            Spacer(modifier = Modifier.height(12.dp))
            
            PawTextField(
                value = state.ownerFullName,
                onValueChange = { viewModel.onEvent(PetRegisterEvent.OnOwnerFullNameChanged(it)) },
                placeholder = "Nombre completo"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PawTextField(
                value = state.ownerPhone,
                onValueChange = { viewModel.onEvent(PetRegisterEvent.OnOwnerPhoneChanged(it)) },
                placeholder = "Teléfono"
            )
            Spacer(modifier = Modifier.height(12.dp))
            PawTextField(
                value = state.ownerEmail,
                onValueChange = { viewModel.onEvent(PetRegisterEvent.OnOwnerEmailChanged(it)) },
                placeholder = "Email"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onEvent(PetRegisterEvent.SavePet) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Registro", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PawTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
    )
}
