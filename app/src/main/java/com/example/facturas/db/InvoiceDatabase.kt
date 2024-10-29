package com.example.facturas.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.facturas.models.Service

@Database(entities = [Service::class], version = 1)
abstract class InvoiceDatabase: RoomDatabase() {

    companion object {
        const val NAME = "invoice_db"
    }

    abstract fun serviceDao(): ServiceDao
}