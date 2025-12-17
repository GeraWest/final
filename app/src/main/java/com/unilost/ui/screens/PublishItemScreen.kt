@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun PublishItemScreen(
    onPublished: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Perdido") }

    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf("user") }

    var categorias by remember { mutableStateOf(listOf<String>()) }
    var categoriaSeleccionada by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }

    val areas = listOf(
        "Cafetería",
        "Edificio A",
        "Edificio F",
        "CGTI",
        "Canchas",
        "Auditorio",
        "Edificio V"
    )
    var areaSeleccionada by remember { mutableStateOf("") }
    var expandedArea by remember { mutableStateOf(false) }

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect

        firestore.collection("roles").document(uid)
            .get()
            .addOnSuccessListener {
                role = it.getString("role") ?: "user"
            }

        firestore.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                categorias = result.documents.mapNotNull { it.getString("nombre") }
                if (categorias.isNotEmpty()) {
                    categoriaSeleccionada = categorias.first()
                }
            }
    }

    if (role != "admin") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No tienes permisos para publicar",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(16.dp)
    ) {

        Text(
            "Publicar nuevo objeto",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BlueMain
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del objeto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedCategoria,
            onExpandedChange = { expandedCategoria = !expandedCategoria }
        ) {
            OutlinedTextField(
                value = categoriaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expandedCategoria)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCategoria,
                onDismissRequest = { expandedCategoria = false }
            ) {
                categorias.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            categoriaSeleccionada = it
                            expandedCategoria = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expandedArea,
            onExpandedChange = { expandedArea = !expandedArea }
        ) {
            OutlinedTextField(
                value = areaSeleccionada,
                onValueChange = {},
                readOnly = true,
                label = { Text("Área") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expandedArea)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedArea,
                onDismissRequest = { expandedArea = false }
            ) {
                areas.forEach { area ->
                    DropdownMenuItem(
                        text = { Text(area) },
                        onClick = {
                            areaSeleccionada = area
                            expandedArea = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Fecha (yyyy-mm-dd)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                error = null

                if (name.isBlank() || categoriaSeleccionada.isBlank() || areaSeleccionada.isBlank()) {
                    error = "Nombre, categoría y área son obligatorios"
                    return@Button
                }

                loading = true
                val uid = auth.currentUser?.uid ?: "guest"

                val itemData = hashMapOf(
                    "name" to name,
                    "description" to desc,
                    "category" to categoriaSeleccionada,
                    "area" to areaSeleccionada,
                    "date" to if (date.isBlank()) "Hoy" else date,
                    "type" to type,
                    "ownerId" to uid,
                    "timestamp" to System.currentTimeMillis()
                )

                val batch = firestore.batch()
                val itemRef = firestore.collection("items").document()
                batch.set(itemRef, itemData)

                val categoriaItemRef = firestore
                    .collection("categorias")
                    .document(categoriaSeleccionada)
                    .collection("items")
                    .document(itemRef.id)
                batch.set(categoriaItemRef, itemData)

                firestore.collection("area")
                    .whereEqualTo("nombre", areaSeleccionada)
                    .limit(1)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty) {
                            val areaDoc = result.documents.first()
                            val areaItemRef = areaDoc.reference
                                .collection("items")
                                .document(itemRef.id)
                            batch.set(areaItemRef, itemData)
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                loading = false
                                name = ""
                                desc = ""
                                date = ""
                                onPublished()
                            }
                            .addOnFailureListener {
                                loading = false
                                error = it.message
                            }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = BlueMain)
        ) {
            Text(if (loading) "Publicando..." else "Publicar", color = Color.White)
        }

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
