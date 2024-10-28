package com.example.facturas.models

data class Invoice (
    val id: String,
    val date: String,
    val expiration: String,
    val services: List<Service>,
    val total: Double = services.sumOf { it.total },
    val iban: String
)