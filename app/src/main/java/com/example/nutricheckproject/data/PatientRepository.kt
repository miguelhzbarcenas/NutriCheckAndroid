package com.example.nutricheckproject.data

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PatientRepository(private val patientDao: PatientDao) {
    val allPatients: Flow<List<Patient>> = patientDao.getAllPatients()

    fun getPatientById(patientId: UUID): Flow<Patient?> {
        return patientDao.getPatientById(patientId)
    }

    suspend fun insert(patient: Patient) {
        patientDao.insert(patient)
    }

    suspend fun update(patient: Patient) {
        patientDao.update(patient)
    }

    suspend fun delete(patient: Patient) {
        patientDao.delete(patient)
    }
}
