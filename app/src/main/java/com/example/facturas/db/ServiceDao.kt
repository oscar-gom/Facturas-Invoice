package com.example.facturas.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.facturas.models.Service

@Dao
interface ServiceDao {

    @Query("SELECT * FROM Service")
    fun getAllServices(): LiveData<List<Service>>

    @Upsert
    fun addService(service: Service)

    @Delete
    fun deleteService(service: Service)
}