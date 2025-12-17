@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

// 🎨 Paleta unificada
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFD9EAF7)

data class ItemModel(
    val id: String = "",
    val name: String = "Sin nombre",
    val place: String = "Lugar desconocido",
    val date: String = "Fecha desconocida"
)

@Composable
fun ItemsListScreen(
    onOpenDetail: (String) -> Unit,
    onOpenPublish: () -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var items by remember { mutableStateOf<List<ItemModel>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var role by remember { mutableStateOf("user") } // Estado para el rol

    // ░░ Obtener rol del usuario
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    // ░░ Firestore Listener para items
    DisposableEffect(Unit) {
        val registration: ListenerRegistration = firestore.collection("items")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    errorMessage = error.message ?: "Error desconocido al leer Firestore"
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val id = doc.id
                    val name = doc.getString("name") ?: "Sin nombre"
                    val place = doc.getString("place") ?: "Lugar desconocido"
                    val date = doc.getString("date") ?: "Fecha desconocida"
                    ItemModel(id = id, name = name, place = place, date = date)
                } ?: emptyList()

                items = list
            }

        onDispose { registration.remove() }
    }

    // ░░ UI ░░
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ⭐ Título
            Text(
                "Objetos publicados",
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.CenterHorizontally),
                color = BlueMain,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            // ⭐ Mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            // ⭐ LISTA DE TARJETAS
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(items) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        onClick = { onOpenDetail(item.id) }
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {

                            // ⭐ Solo nombre del objeto
                            Text(
                                text = item.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlueMain
                            )
                        }
                    }
                }
            }
        }

        // ⭐ FAB solo visible si es admin
        if (role == "admin") {
            FloatingActionButton(
                onClick = onOpenPublish,
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd),
                containerColor = BlueMain,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Publicar")
            }
        }
    }
}
