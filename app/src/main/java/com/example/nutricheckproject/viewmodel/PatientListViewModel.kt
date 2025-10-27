package com.example.nutricheckproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheckproject.data.Patient
import com.example.nutricheckproject.data.PatientRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PatientListViewModel(private val repository: PatientRepository) : ViewModel() {

    val allPatients: StateFlow<List<Patient>> = repository.allPatients
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun delete(patient: Patient) = viewModelScope.launch {
        repository.delete(patient)
    }
}
