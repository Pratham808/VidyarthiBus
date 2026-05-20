package com.example.vidyarthi_bus.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidyarthi_bus.domain.model.AutoContact
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedAutoViewModel @Inject constructor() : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("auto_contacts")

    private val _contacts = MutableStateFlow<List<AutoContact>>(emptyList())
    val contacts: StateFlow<List<AutoContact>> = _contacts.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(AutoContact::class.java) }
                _contacts.value = list
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addContact(name: String, phone: String, village: String) {
        val id = database.push().key ?: return
        val contact = AutoContact(id, name, phone, village)
        database.child(id).setValue(contact)
    }

    fun deleteContact(id: String) {
        database.child(id).removeValue()
    }
}