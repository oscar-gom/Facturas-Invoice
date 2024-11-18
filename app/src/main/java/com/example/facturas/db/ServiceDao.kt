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

    @Query("SELECT * FROM Service WHERE serviceId = :serviceId")
    fun getService(serviceId: Int): Service

    @Query("SELECT * FROM Service WHERE description LIKE :description")
    fun getServiceByDescription(description: String): Service

    @Query("SELECT EXISTS(SELECT 1 FROM Service WHERE description = :description)")
    fun isDescriptionExists(description: String): Boolean

    @Query("SELECT serviceId FROM Service ORDER BY serviceId DESC LIMIT 1")
    fun getLastServiceId(): Int

    @Upsert
    fun addService(service: Service)

    @Delete
    fun deleteService(service: Service)
}