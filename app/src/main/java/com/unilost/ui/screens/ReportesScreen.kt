@file:OptIn(ExperimentalMaterial3Api::class)
package com.unilost.ui.screens

import android.graphics.pdf.PdfDocument
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// 🎨 Colores
private val BlueMain = Color(0xFF21527A)
private val BlueBackground = Color(0xFFD9EAF7)

/* =======================
   MODELO DE DATOS
   ======================= */
data class ItemReporte(
    val name: String,
    val place: String,
    val date: String,
    val description: String
)

@Composable
fun ReportesScreen() {

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    var role by remember { mutableStateOf("user") }
    var loading by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf<String?>(null) }

    /* =======================
       VERIFICAR ROL
       ======================= */
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("roles")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                role = doc.getString("role") ?: "user"
            }
    }

    /* =======================
       BLOQUEO NO ADMIN
       ======================= */
    if (role != "admin") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Acceso solo para administradores",
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        return
    }

    /* =======================
       UI PRINCIPAL
       ======================= */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Generar Reporte General",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BlueMain
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loading = true
                mensaje = null

                firestore.collection("items")
                    .get()
                    .addOnSuccessListener { result ->

                        val items = result.documents.map {
                            ItemReporte(
                                name = it.getString("name") ?: "",
                                place = it.getString("place") ?: "",
                                date = it.getString("date") ?: "",
                                description = it.getString("description") ?: ""
                            )
                        }

                        val uid = auth.currentUser?.uid ?: "admin"

                        val reporteData = hashMapOf(
                            "titulo" to "Reporte general de objetos",
                            "generadoPor" to uid,
                            "totalItems" to items.size,
                            "timestamp" to System.currentTimeMillis()
                        )

                        firestore.collection("reportes")
                            .add(reporteData)
                            .addOnSuccessListener {

                                generarPDF(items, uid)

                                loading = false
                                mensaje = "Reporte generado correctamente"
                            }
                            .addOnFailureListener { e ->
                                loading = false
                                mensaje = e.message
                            }
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        mensaje = e.message
                    }
            },
            colors = ButtonDefaults.buttonColors(containerColor = BlueMain),
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            Text(
                if (loading) "Generando reporte..." else "Generar Reporte",
                color = Color.White
            )
        }

        mensaje?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = if (it.contains("correctamente")) Color(0xFF2E7D32)
                else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/* =======================
   GENERADOR DE PDF
   ======================= */
fun generarPDF(items: List<ItemReporte>, uid: String) {

    val pdfDocument = PdfDocument()
    val paint = android.graphics.Paint()

    var pageNumber = 1
    var y = 130

    fun nuevaPagina(): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Reporte General de Objetos Perdidos", 40f, 50f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Generado por: $uid", 40f, 80f, paint)

        val fecha = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        ).format(Date())

        canvas.drawText("Fecha: $fecha", 40f, 100f, paint)

        return page
    }

    var page = nuevaPagina()
    var canvas = page.canvas

    items.forEachIndexed { index, item ->

        if (y > 780) {
            pdfDocument.finishPage(page)
            pageNumber++
            y = 130
            page = nuevaPagina()
            canvas = page.canvas
        }

        paint.isFakeBoldText = true
        paint.textSize = 15f
        canvas.drawText("${index + 1}. ${item.name}", 40f, y.toFloat(), paint)

        paint.isFakeBoldText = false
        paint.textSize = 13f

        y += 20
        canvas.drawText("Lugar: ${item.place}", 60f, y.toFloat(), paint)

        y += 20
        canvas.drawText("Fecha: ${item.date}", 60f, y.toFloat(), paint)

        y += 20
        canvas.drawText("Descripción: ${item.description}", 60f, y.toFloat(), paint)

        y += 30
    }

    pdfDocument.finishPage(page)

    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Reporte_Objetos_${System.currentTimeMillis()}.pdf"
    )

    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()
}
