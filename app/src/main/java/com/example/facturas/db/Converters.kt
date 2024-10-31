package com.example.facturas.db

import androidx.room.TypeConverter
import com.example.facturas.models.Service
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromServiceList(services: List<Service>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Service>>() {}.type
        return gson.toJson(services, type)
    }

    @TypeConverter
    fun toServiceList(services: String): List<Service> {
        val gson = Gson()
        val type = object : TypeToken<List<Service>>() {}.type
        return gson.fromJson(services, type)
    }
}