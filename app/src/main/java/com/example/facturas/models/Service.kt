package com.example.facturas.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Service(
    @PrimaryKey(autoGenerate = true)
    val serviceId: Int = 0,
    val description: String,
    val price: Double,
    val tax: Int,
)