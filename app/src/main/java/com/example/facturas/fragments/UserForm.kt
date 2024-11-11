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

class UserForm : Fragment() {
    val db = MainApplication.database
    val utilities = PersonUtilities()

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

            if (!utilities.isEditTextsCorrect(
                    nameEditText,
                    lastNameEditText,
                    fiscalNumberEditText,
                    addressEditText,
                    cityEditText,
                    cpEditText
                )
            ) {
                return@setOnClickListener
            }

            if (utilities.fiscalNumberCheck(fiscalNumber, fiscalNumberEditText)) {
                return@setOnClickListener
            }

            val user = Person(
                name = name,
                lastName = lastName,
                fiscalNumber = fiscalNumber,
                address = address,
                city = city,
                cp = cpText,
                isUser = true,
                iban = null
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

}