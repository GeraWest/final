
package com.unilost.data.repository

import com.unilost.data.model.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class LocalRepository {
    private val _items = MutableStateFlow<List<LostItem>>(sample())
    val items: StateFlow<List<LostItem>> = _items

    private val users = mutableMapOf<String, String>() // email -> password (demo only)

    fun addUser(email: String, password: String) {
        users[email] = password
    }

    fun login(email: String, password: String): Boolean {
        return users[email] == password
    }

    fun addItem(item: LostItem) {
        val list = _items.value.toMutableList()
        list.add(0, item)
        _items.value = list
    }

    fun updateItem(updated: LostItem) {
        _items.value = _items.value.map { if (it.id == updated.id) updated else it }
    }

    fun deleteItem(id: String) {
        _items.value = _items.value.filter { it.id != id }
    }

    fun findById(id: String): LostItem? = _items.value.find { it.id == id }

    companion object {
        private fun sample() = listOf(
            LostItem(UUID.randomUUID().toString(),"Mochila negra","Mochila con distintivo rojo","Mochilas","Aula 1","2025-11-01","Perdido", null,"u1"),
            LostItem(UUID.randomUUID().toString(),"Llaves","Llavero con etiqueta azul","Llaves","Biblioteca","2025-10-20","Encontrado", null,"u2")
        )
    }
}
