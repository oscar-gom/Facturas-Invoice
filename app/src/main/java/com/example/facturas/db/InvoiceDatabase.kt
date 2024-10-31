package com.example.facturas.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.facturas.models.Invoice
import com.example.facturas.models.Person
import com.example.facturas.models.Service

@Database(entities = [Service::class, Person::class, Invoice::class], version = 1)
@TypeConverters(Converters::class)
abstract class InvoiceDatabase: RoomDatabase() {

    companion object {
        const val NAME = "invoice_db"
    }

    abstract fun serviceDao(): ServiceDao
    abstract fun personDao(): PersonDao
    abstract fun invoiceDao(): InvoiceDao
}