package com.example.facturas.utilities

import android.widget.EditText
import com.example.facturas.MainApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PersonUtilities {
    val db = MainApplication.database
    fun fiscalNumberCheck(
        fiscalNumber: String,
        fiscalNumberEditText: EditText
    ): Boolean {
        for (i in 0 until fiscalNumber.length - 1) {
            if (!fiscalNumber[i].isDigit()) {
                fiscalNumberEditText.error = "El NIF debe tener 8 dígitos y una letra"
                return true
            }
        }

        if (!fiscalNumber[8].isLetter()) {
            fiscalNumberEditText.error = "El NIF debe tener 8 dígitos y una letra"
            return true
        }

        val letter = fiscalNumber[8].uppercaseChar()
        val number = fiscalNumber.substring(0, 8).toInt()

        val letterNum = number % 23
        val letters = "TRWAGMYFPDXBNJZSQVHLCKE"
        val letterCheck = letters[letterNum]

        if (letter != letterCheck) {
            fiscalNumberEditText.error = "El NIF no es válido"
            return true
        }
        var exists = false
        CoroutineScope(Dispatchers.IO).launch {
            exists = db.personDao().isFiscalNumberExists(fiscalNumber)
        }

        if (exists) {
            fiscalNumberEditText.error = "El NIF ya existe"
            return true
        }

        return false
    }

    fun isEditTextsCorrect(
        nameEditText: EditText,
        lastNameEditText: EditText,
        fiscalNumberEditText: EditText,
        addressEditText: EditText,
        cityEditText: EditText,
        cpEditText: EditText
    ): Boolean {
        val name = nameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val fiscalNumber = fiscalNumberEditText.text.toString()
        val address = addressEditText.text.toString()
        val city = cityEditText.text.toString()
        val cp = cpEditText.text.toString()

        if (name.isEmpty()) {
            nameEditText.error = "El nombre es obligatorio"
            return false
        }

        if (lastName.isEmpty()) {
            lastNameEditText.error = "El apellido es obligatorio"
            return false
        }

        if (fiscalNumber.isEmpty()) {
            fiscalNumberEditText.error = "El NIF es obligatorio"
            return false
        }

        if (address.isEmpty()) {
            addressEditText.error = "La dirección es obligatoria"
            return false
        }

        if (city.isEmpty()) {
            cityEditText.error = "La ciudad es obligatoria"
            return false
        }

        if (cp == "00000") {
            cpEditText.error = "El código postal es obligatorio"
            return false
        }

        return true
    }

    fun isIbanEmpty(ibanEditText: EditText): Boolean {
        if (ibanEditText.text.toString().isEmpty()) {
            ibanEditText.error = "El IBAN es obligatorio"
            return false
        }

        return true
    }
}