package com.example.facturas.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.facturas.models.PersonInvoice

@Dao
interface PersonInvoiceDao {
    @Query("SELECT * FROM PersonInvoice")
    fun getAllPersonInvoices(): LiveData<List<PersonInvoice>>

    @Insert
    fun addPersonInvoice(personInvoice: PersonInvoice)

    @Delete
    fun deletePersonInvoice(personInvoice: PersonInvoice)
}