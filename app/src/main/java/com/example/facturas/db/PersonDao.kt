package com.example.facturas.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.facturas.models.Person

@Dao
interface PersonDao {
    @Query("SELECT * FROM Person")
    fun getAllPersons(): LiveData<List<Person>>

    @Upsert
    fun addPerson(person: Person)

    @Query("SELECT * FROM Person WHERE isUser = 1")
    fun getUser(): Person

    @Query("SELECT * FROM Person WHERE isUser = 0")
    fun getClients(): LiveData<Person>

    @Query("SELECT * FROM Person WHERE personId = :personId")
    fun getPersonById(personId: Int): Person

    @Query("SELECT EXISTS(SELECT 1 FROM Person WHERE fiscalNumber = :fiscalNumber)")
    fun isFiscalNumberExists(fiscalNumber: String): Boolean

    @Query("SELECT personId FROM Person WHERE isUser = 0 ORDER BY personId DESC LIMIT 1")
    fun getLastClientId(): Int

    @Delete
    fun deletePerson(person: Person)

    @Query("DELETE FROM Person WHERE isUser = 1")
    fun deleteUser()
}