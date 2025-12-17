@file:OptIn(ExperimentalMaterial3Api::class)

package com.unilost.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unilost.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

// 🎨 Paleta basada en #21527A
private val BlueMain = Color(0xFF21527A)
private val BlueLight = Color(0xFF3A6D9A)
private val BlueBackground = Color(0xFFD9EAF7)

@Composable
fun LoginScreen(
    viewModel: MainViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "Iniciar sesión",
            fontSize = 34.sp,
            color = BlueMain,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo institucional", color = BlueMain) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BlueMain) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueMain,
                unfocusedBorderColor = BlueLight,
                cursorColor = BlueMain,
                focusedLabelColor = BlueMain,
                unfocusedLabelColor = BlueLight
            )
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña", color = BlueMain) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BlueMain) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueMain,
                unfocusedBorderColor = BlueLight,
                cursorColor = BlueMain,
                focusedLabelColor = BlueMain,
                unfocusedLabelColor = BlueLight
            )
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                loading = true
                error = null

                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        loading = false
                        onLoginSuccess()
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        error = e.message ?: "Error al iniciar sesión"
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueMain,
                contentColor = Color.White
            )
        ) {
            Text("Entrar", fontSize = 18.sp)
        }

        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onRegister) {
            Text("Crear cuenta", color = BlueMain)
        }

        error?.let {
            Text(
                it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
