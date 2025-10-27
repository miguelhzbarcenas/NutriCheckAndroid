package com.example.nutricheckproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheckproject.data.Patient
import com.example.nutricheckproject.data.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class PatientDetailViewModel(private val repository: PatientRepository) : ViewModel() {

    private val _patientState = MutableStateFlow<Patient?>(null)
    val patientState: StateFlow<Patient?> = _patientState.asStateFlow()

    private val _finishEvent = MutableStateFlow(false)
    val finishEvent: StateFlow<Boolean> = _finishEvent.asStateFlow()

    fun loadPatient(patientId: UUID) {
        viewModelScope.launch {
            repository.getPatientById(patientId).collectLatest {
                _patientState.value = it
            }
        }
    }

    fun savePatient(patient: Patient) {
        viewModelScope.launch {
            if (_patientState.value == null) {
                repository.insert(patient)
            } else {
                repository.update(patient)
            }
            _finishEvent.value = true
        }
    }

    fun deletePatient() {
        _patientState.value?.let { patient ->
            viewModelScope.launch {
                repository.delete(patient)
                _finishEvent.value = true
            }
        }
    }
}
