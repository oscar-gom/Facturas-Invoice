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
import com.example.facturas.utilities.PersonUtilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClientForm : Fragment() {
    val db = MainApplication.database
    val utilities = PersonUtilities()

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
        val ibanEditText = "null"

        cpEditText.filters = arrayOf(InputFilter.LengthFilter(5))
        fiscalNumberEditText.filters = arrayOf(InputFilter.LengthFilter(9))

        saveButton.setOnClickListener {
            saveClient(
                nameEditText,
                lastNameEditText,
                fiscalNumberEditText,
                addressEditText,
                cityEditText,
                cpEditText,
            )
        }

        return view
    }

    private fun saveClient(
        nameEditText: EditText,
        lastNameEditText: EditText,
        fiscalNumberEditText: EditText,
        addressEditText: EditText,
        cityEditText: EditText,
        cpEditText: EditText,
    ) {
        val name = nameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val fiscalNumber = fiscalNumberEditText.text.toString()
        val address = addressEditText.text.toString()
        val city = cityEditText.text.toString()
        val cp = cpEditText.text.toString()

        if (!utilities.isEditTextsCorrect(
                nameEditText,
                lastNameEditText,
                fiscalNumberEditText,
                addressEditText,
                cityEditText,
                cpEditText
            )
        ) return

        if (utilities.fiscalNumberCheck(fiscalNumber, fiscalNumberEditText)) return

        val person = Person(
            isUser = false,
            name = name,
            lastName = lastName,
            fiscalNumber = fiscalNumber,
            address = address,
            city = city,
            cp = cp,
            iban = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.personDao().addPerson(person)
            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(
                    requireContext(),
                    "¡Exito! Cliente añadido a la agenda",
                    Toast.LENGTH_SHORT
                ).show()
                nameEditText.text.clear()
                lastNameEditText.text.clear()
                fiscalNumberEditText.text.clear()
                addressEditText.text.clear()
                cityEditText.text.clear()
                cpEditText.text.clear()
            }
        }
    }




}