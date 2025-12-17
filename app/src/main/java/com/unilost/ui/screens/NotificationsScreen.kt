@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun NotificationsScreen() {

    val firestore = FirebaseFirestore.getInstance()
    var notifs by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        firestore.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notifs = snapshot.documents.mapNotNull {
                        val text = it.getString("text") ?: return@mapNotNull null
                        val ts = it.getLong("timestamp") ?: 0L
                        NotificationItem(it.id, text, ts)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones", color = Color.White, fontSize = 22.sp) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = BlueMain)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier.fillMaxSize().background(BlueBackground).padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (notifs.isEmpty()) {
                Text("No hay notificaciones", color = BlueMain)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    items(notifs) { notif ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    firestore.collection("notifications")
                                        .document(notif.id).delete()
                                },
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Text(
                                notif.text,
                                modifier = Modifier.padding(16.dp),
                                color = BlueMain,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

data class NotificationItem(
    val id: String,
    val text: String,
    val timestamp: Long
)
