package com.example.nutricheckproject.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: UUID = UUID.randomUUID(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "paternalLN")
    val paternalLN: String,

    @ColumnInfo(name = "maternalLN")
    val maternalLN: String,

    @ColumnInfo(name = "birthday")
    val birthday: Date?,

    @ColumnInfo(name = "gender")
    val gender: String?,

    @ColumnInfo(name = "height")
    val height: Double?,

    @ColumnInfo(name = "weight")
    val weight: Double?,

    @ColumnInfo(name = "notes")
    val notes: String?,

    @ColumnInfo(name = "creation")
    val creation: Date = Date()
)