
package com.unilost.data.model

data class LostItem(
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val place: String,
    val date: String,
    val type: String, // "Perdido" or "Encontrado"
    val imagePath: String? = null,
    val ownerId: String? = null
)
