package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientForm : Fragment() {
    val db = MainApplication.database

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client_form, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.client_name)
        val lastNameEditText = view.findViewById<EditText>(R.id.client_last_name)
        val fiscalNumberEditText = view.findViewById<EditText>(R.id.client_fiscal_number)
        val addressEditText = view.findViewById<EditText>(R.id.client_address)
        val cityEditText = view.findViewById<EditText>(R.id.client_city)
        val cpEditText = view.findViewById<EditText>(R.id.client_cp)
        val saveButton = view.findViewById<Button>(R.id.save_client_button)

        cpEditText.filters = arrayOf(InputFilter.LengthFilter(5))

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val fiscalNumber = fiscalNumberEditText.text.toString()
            val address = addressEditText.text.toString()
            val city = cityEditText.text.toString()
            val cp = cpEditText.text.toString().toIntOrNull() ?: 0

            if (name.isEmpty()) {
                nameEditText.error = "El nombre es obligatorio"
                return@setOnClickListener
            }

            if (lastName.isEmpty()) {
                lastNameEditText.error = "El apellido es obligatorio"
                return@setOnClickListener
            }

            if (fiscalNumber.isEmpty()) {
                fiscalNumberEditText.error = "El NIF es obligatorio"
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                addressEditText.error = "La dirección es obligatoria"
                return@setOnClickListener
            }

            if (city.isEmpty()) {
                cityEditText.error = "La ciudad es obligatoria"
                return@setOnClickListener
            }

            if (cp == 0) {
                cpEditText.error = "El código postal es obligatorio"
                return@setOnClickListener
            }

            if (fiscalNumber.length != 9) {
                fiscalNumberEditText.error = "El NIF debe tener 9 dígitos"
                return@setOnClickListener
            }

            if (fiscalNumberCheck(fiscalNumber, fiscalNumberEditText)) return@setOnClickListener

            val person = Person(
                isUser = false,
                name = name,
                lastName = lastName,
                fiscalNumber = fiscalNumber,
                address = address,
                city = city,
                cp = cp
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.personDao().addPerson(person)
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(requireContext(), "¡Exito! Cliente añadido a la agenda", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun fiscalNumberCheck(
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
        return false
    }
}