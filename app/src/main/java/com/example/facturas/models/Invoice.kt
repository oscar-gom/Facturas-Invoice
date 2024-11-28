package com.example.facturas.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class Invoice (
    @PrimaryKey(autoGenerate = true)
    val invoiceId: Int = 0,
    val invoiceNum: String,
    val date: LocalDate = LocalDate.now(),
    @Embedded(prefix = "client_") val client: PersonInvoice,
    @Embedded(prefix = "user_") val user: PersonInvoice,
    val services: List<ServiceInvoice>,
    val subtotal: Double = services.sumOf { it.subTotal },
    val total: Double = services.sumOf { it.total }
)