package com.example.facturas.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
data class Service(
    @PrimaryKey(autoGenerate = true)
    val serviceId: Int = 0,
    val description: String,
    val price: Double,
    val tax: Double,
)