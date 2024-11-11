package com.example.facturas.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.facturas.models.ServiceInvoice

@Dao
interface ServiceInvoiceDao {
    @Query("SELECT * FROM ServiceInvoice")
    fun getAllServiceInvoices(): LiveData<List<ServiceInvoice>>

    @Insert
    fun addServiceInvoice(serviceInvoice: ServiceInvoice)

    @Delete
    fun deleteServiceInvoice(serviceInvoice: ServiceInvoice)
}