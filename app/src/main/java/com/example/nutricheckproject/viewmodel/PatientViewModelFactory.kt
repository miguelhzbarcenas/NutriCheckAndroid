package com.example.nutricheckproject.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nutricheckproject.data.AppDatabase
import com.example.nutricheckproject.data.PatientRepository

class PatientViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository =
            PatientRepository(AppDatabase.Companion.getDatabase(application).patientDao())

        if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(PatientDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
