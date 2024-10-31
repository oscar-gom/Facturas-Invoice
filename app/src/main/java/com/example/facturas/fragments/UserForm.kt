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
import androidx.lifecycle.Observer
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserForm : Fragment() {
    val db = MainApplication.database

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_form, container, false)

        val nameEditText = view.findViewById<EditText>(R.id.user_name)
        val lastNameEditText = view.findViewById<EditText>(R.id.user_last_name)
        val fiscalNumberEditText = view.findViewById<EditText>(R.id.user_fiscal_number)
        val addressEditText = view.findViewById<EditText>(R.id.user_address)
        val cityEditText = view.findViewById<EditText>(R.id.user_city)
        val cpEditText = view.findViewById<EditText>(R.id.user_cp)
        val saveButton = view.findViewById<Button>(R.id.save_user_button)

        fiscalNumberEditText.filters = arrayOf(InputFilter.LengthFilter(9))
        cpEditText.filters = arrayOf(InputFilter.LengthFilter(5))

        db.personDao().getUser().observe(viewLifecycleOwner) { user ->
            user?.let {
                nameEditText.setText(it.name)
                lastNameEditText.setText(it.lastName)
                fiscalNumberEditText.setText(it.fiscalNumber)
                addressEditText.setText(it.address)
                cityEditText.setText(it.city)
                cpEditText.setText(it.cp)
            }
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val fiscalNumber = fiscalNumberEditText.text.toString()
            val address = addressEditText.text.toString()
            val city = cityEditText.text.toString()
            val cpText = cpEditText.text.toString()

            if (name.isEmpty()) {
                nameEditText.error = "El nombre es obligatorio"
                return@setOnClickListener
            }
            if (lastName.isEmpty()) {
                lastNameEditText.error = "El apellido es obligatorio"
                return@setOnClickListener
            }
            if (fiscalNumber.isEmpty()) {
                fiscalNumberEditText.error = "El número fiscal es obligatorio"
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
            if (cpText.isEmpty()) {
                cpEditText.error = "El código postal es obligatorio"
                return@setOnClickListener
            }

            if (cpText == "00000"){
                cpEditText.error = "El código postal no puede ser 00000"
                return@setOnClickListener
            }

            if (fiscalNumberCheck(fiscalNumber, fiscalNumberEditText)) return@setOnClickListener

            val user = Person(
                name = name,
                lastName = lastName,
                fiscalNumber = fiscalNumber,
                address = address,
                city = city,
                cp = cpText,
                isUser = true
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.personDao().deleteUser()
                db.personDao().addPerson(user)
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        requireContext(),
                        "¡Exito! Usuario guardado", Toast.LENGTH_SHORT
                    ).show()
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