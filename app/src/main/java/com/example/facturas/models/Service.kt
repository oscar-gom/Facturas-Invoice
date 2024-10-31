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
    val discount: Double = 0.0,
    val units: Int,
    val tax: Double,
    val subTotal: Double = BigDecimal((price * ((100 - discount) / 100)) * units).setScale(
        2,
        RoundingMode.HALF_EVEN
    ).toDouble(),
    val total: Double = BigDecimal(subTotal * (((tax) / 100) + 1)).setScale(
        2,
        RoundingMode.HALF_EVEN
    ).toDouble()
)