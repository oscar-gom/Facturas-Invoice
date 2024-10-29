package com.example.facturas.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Invoice (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val date: String = LocalDate.now().toString(),
    val client: Person,
    val services: List<Service>,
    val paymentMethod: PaymentMethod,
    val iban: String?,
    val total: Double = services.sumOf { it.total }
)