package com.example.facturas

import android.app.Application
import androidx.room.Room
import com.example.facturas.db.InvoiceDatabase

class MainApplication : Application() {
    companion object {
        lateinit var database: InvoiceDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            InvoiceDatabase::class.java,
            InvoiceDatabase.NAME
        ).build()
    }
}