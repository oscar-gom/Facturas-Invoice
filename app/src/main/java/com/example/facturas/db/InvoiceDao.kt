package com.example.facturas.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.facturas.models.Invoice

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM Invoice")
    fun getAllInvoices(): LiveData<List<Invoice>>

    @Upsert
    fun addInvoice(invoice: Invoice)

    @Delete
    fun deleteInvoice(invoice: Invoice)
}