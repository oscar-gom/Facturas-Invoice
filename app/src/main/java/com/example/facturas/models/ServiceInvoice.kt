package com.example.facturas.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
data class ServiceInvoice(
    @PrimaryKey(autoGenerate = true)
    val serviceInvoiceId: Int,
    val description: String,
    val price: Double,
    val tax: Int,
    val units: Int = 1,
    val discount: Int = 0,
    val subTotal: Double = BigDecimal((price * ((100 - discount) / 100)) * units).setScale(
        2,
        RoundingMode.HALF_EVEN
    ).toDouble(),
    val total: Double = BigDecimal(subTotal * (((tax) / 100) + 1)).setScale(
        2,
        RoundingMode.HALF_EVEN
    ).toDouble()
)