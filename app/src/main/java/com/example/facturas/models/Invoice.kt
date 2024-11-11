package com.example.facturas.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Invoice (
    @PrimaryKey(autoGenerate = true)
    val invoiceId: Int = 0,
    val date: LocalDate = LocalDate.now(),
    @Embedded val client: Person,
    val services: List<ServiceInvoice>,
    val paymentMethod: PaymentMethod,
    val iban: String?,
    val total: Double = services.sumOf { it.total }
)