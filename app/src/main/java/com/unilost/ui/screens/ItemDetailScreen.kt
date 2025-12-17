@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 🎨 Paleta azul profesional
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFE6F0FA)

@Composable
fun ItemDetailScreen(
    id: String,
    onBack: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var item by remember { mutableStateOf<Map<String, Any>?>(null) }
    var loading by remember { mutableStateOf(true) }
    var role by remember { mutableStateOf("user") }

    // 🔐 Obtener rol
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    // 📦 Obtener item
    LaunchedEffect(id) {
        firestore.collection("items").document(id)
            .get()
            .addOnSuccessListener { doc ->
                item = doc.data
                loading = false
            }
            .addOnFailureListener {
                loading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = item?.get("name")?.toString() ?: "Detalle",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BlueMain
                )
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BlueMain)
            }
            return@Scaffold
        }

        item?.let { it ->

            val name = it["name"]?.toString() ?: ""
            val area = it["area"]?.toString() ?: ""   // ✅ CORREGIDO
            val date = it["date"]?.toString() ?: ""
            val desc = it["description"]?.toString() ?: "-"
            val ownerId = it["ownerId"]?.toString() ?: ""

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueBackground)
                    .padding(padding)
                    .padding(16.dp)
            ) {

                // 📌 Tarjeta del objeto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {

                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueMain
                        )

                        Spacer(Modifier.height(12.dp))

                        Text("Área:", fontWeight = FontWeight.Bold, color = BlueLight)
                        Text(area)

                        Spacer(Modifier.height(8.dp))

                        Text("Fecha:", fontWeight = FontWeight.Bold, color = BlueLight)
                        Text(date)

                        Spacer(Modifier.height(12.dp))

                        // 🔒 SOLO ADMINS ven la descripción
                        if (role == "admin") {
                            Text(
                                "Descripción:",
                                fontWeight = FontWeight.Bold,
                                color = BlueLight
                            )
                            Text(desc)
                        } else {
                            Text(
                                "La descripción solo está disponible con el administrador.",
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // 🗑️ Eliminar (solo admin o dueño)
                if (role == "admin" || ownerId == auth.currentUser?.uid) {
                    OutlinedButton(
                        onClick = {
                            firestore.collection("items").document(id).delete()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BlueMain
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Objeto no encontrado", color = Color.Red)
        }
    }
}
