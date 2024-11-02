package com.example.facturas.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.math.RoundingMode

@Entity
data class ServiceInvoice(
    @PrimaryKey(autoGenerate = true)
    val serviceId: Int,
    val discount: Double = 0.0,
    val units: Int = 1,
    @Embedded
    val service: Service,
    val subTotal: Double = BigDecimal((service.price * ((100 - discount) / 100)) * units).setScale(
        2,
        RoundingMode.HALF_EVEN
    ).toDouble(),
    val total: Double = BigDecimal(subTotal * (((service.tax) / 100) + 1)).setScale(
        2,
        RoundingMode.HALF_EVEN
    ).toDouble()
)