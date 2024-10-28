package com.example.facturas.models

data class Service (
    val name: String,
    val price: Double,
    val discount: Double,
    val units: Int,
    val tax: Double,
    val subTotal: Double = price - discount,
    val total: Double = subTotal + tax
)