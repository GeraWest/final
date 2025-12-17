package com.unilost.data.model

data class Reporte(
    val id: String = "",
    val itemId: String = "",       // referencia a LostItem.id
    val tipo: String = "",         // Perdido / Encontrado
    val fecha: String = "",
    val descripcion: String? = null
)