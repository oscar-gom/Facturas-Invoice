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
    fun getUser(): LiveData<Person>

    @Delete
    fun deletePerson(person: Person)

    @Query("DELETE FROM Person WHERE isUser = 1")
    fun deleteUser()
}