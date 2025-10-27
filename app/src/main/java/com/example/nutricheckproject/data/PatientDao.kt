package com.example.nutricheckproject.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface PatientDao {
    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :patientId")
    fun getPatientById(patientId: UUID): Flow<Patient?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(patient: Patient)

    @Update
    suspend fun update(patient: Patient)

    @Delete
    suspend fun delete(patient: Patient)
}
