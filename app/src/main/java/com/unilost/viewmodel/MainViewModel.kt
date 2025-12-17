
package com.unilost.viewmodel

import androidx.lifecycle.ViewModel
import com.unilost.data.model.LostItem
import com.unilost.data.repository.LocalRepository
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MainViewModel : ViewModel() {
    private val repo = LocalRepository()
    val itemsFlow: StateFlow<List<LostItem>> = repo.items

    // minimal auth state (in-memory)
    private var currentUserEmail: String? = null
    fun register(email:String, pass:String): Boolean {
        repo.addUser(email, pass)
        currentUserEmail = email
        return true
    }
    fun login(email:String, pass:String): Boolean {
        val ok = repo.login(email, pass)
        if (ok) currentUserEmail = email
        return ok
    }
    fun logout() {
        currentUserEmail = null
    }
    fun isAuthenticated() = currentUserEmail != null
    fun currentUser() = currentUserEmail

    fun publishItem(
        name:String, desc:String?, category:String, place:String, date:String, type:String
    ) {
        val item = LostItem(UUID.randomUUID().toString(), name, desc, category, place, date, type, null, currentUserEmail)
        repo.addItem(item)
    }
    fun getItem(id:String): LostItem? = repo.findById(id)
    fun deleteItem(id:String) = repo.deleteItem(id)
}
