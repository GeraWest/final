@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 🔥 IMPORTS QUE FALTABAN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 🎨 Paleta de colores
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun HomeScreen(
    onOpenList: () -> Unit,
    onOpenPublish: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenReportes: () -> Unit
) {
    var selected by remember { mutableStateOf(0) }
    var role by remember { mutableStateOf("user") }

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // 🔐 Verificar rol (solo admin ve reportes)
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = BlueMain) {

                NavigationBarItem(
                    selected = selected == 0,
                    onClick = { selected = 0; onOpenList() },
                    icon = { Icon(Icons.Default.Home, tint = Color.White, contentDescription = null) },
                    label = { Text("Buscar", color = Color.White) }
                )

                NavigationBarItem(
                    selected = selected == 1,
                    onClick = { selected = 1; onOpenPublish() },
                    icon = { Icon(Icons.Default.Add, tint = Color.White, contentDescription = null) },
                    label = { Text("Publicar", color = Color.White) }
                )

                NavigationBarItem(
                    selected = selected == 2,
                    onClick = { selected = 2; onOpenNotifications() },
                    icon = { Icon(Icons.Default.Notifications, tint = Color.White, contentDescription = null) },
                    label = { Text("Notifs", color = Color.White) }
                )

                NavigationBarItem(
                    selected = selected == 3,
                    onClick = { selected = 3; onOpenProfile() },
                    icon = { Icon(Icons.Default.Person, tint = Color.White, contentDescription = null) },
                    label = { Text("Perfil", color = Color.White) }
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {

            // 🔵 ENCABEZADO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .background(BlueMain, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "Bienvenido a UniLost 👋",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Busca y publica objetos perdidos/encontrados.",
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(Modifier.height(25.dp))

            // 🔲 TARJETA DE ACCIONES
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {

                Column {

                    Text(
                        "¿Qué deseas hacer?",
                        fontWeight = FontWeight.Bold,
                        color = BlueMain,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = onOpenList,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Ver objetos publicados", color = Color.White)
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onOpenPublish,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Publicar un objeto", color = BlueMain)
                    }

                    // 🧾 SOLO ADMIN
                    if (role == "admin") {
                        Spacer(Modifier.height(16.dp))

                        Button(
                            onClick = onOpenReportes,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueLight),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Generar Reporte", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
