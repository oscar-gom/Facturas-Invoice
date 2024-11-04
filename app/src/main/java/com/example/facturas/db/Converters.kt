package com.example.facturas.db

import androidx.room.TypeConverter
import com.example.facturas.models.ServiceInvoice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromServiceList(services: List<ServiceInvoice>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ServiceInvoice>>() {}.type
        return gson.toJson(services, type)
    }

    @TypeConverter
    fun toServiceList(services: String): List<ServiceInvoice> {
        val gson = Gson()
        val type = object : TypeToken<List<ServiceInvoice>>() {}.type
        return gson.fromJson(services, type)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(formatter)
    }

    @TypeConverter
    fun toLocalDate(dateString: String): LocalDate {
        return LocalDate.parse(dateString, formatter)
    }
}