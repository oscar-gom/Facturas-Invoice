package com.example.facturas.models

import java.math.BigDecimal
import java.math.RoundingMode

data class Service (
    val name: String,
    val price: Double,
    val discount: Double,
    val units: Int,
    val tax: Double,
    val subTotal: Double = BigDecimal(price * ((100 - discount) / 100)).setScale(2, RoundingMode.HALF_EVEN).toDouble(),
    val total: Double = BigDecimal(subTotal * (((tax) / 100) + 1)).setScale(2, RoundingMode.HALF_EVEN).toDouble()
)