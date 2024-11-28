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
    @Embedded val client: PersonInvoice,
    @Embedded val user: PersonInvoice,
    val services: List<ServiceInvoice>,
    val paymentMethod: PaymentMethod,
    val invoiceIban: String?,
    val total: Double = services.sumOf { it.total }
)