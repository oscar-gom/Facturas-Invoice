package com.example.facturas.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonInvoice(
    @PrimaryKey(autoGenerate = true)
    val personInvoiceId: Int = 0,
    val isUser: Boolean,
    val name: String,
    val lastName: String,
    val fiscalNumber: String,
    val address: String,
    val city: String,
    val cp: String
)
