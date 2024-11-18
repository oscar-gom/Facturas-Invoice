package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
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
    private val utilities = PersonUtilities()

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
        val ibanEditText = view.findViewById<EditText>(R.id.user_iban)
        val saveButton = view.findViewById<Button>(R.id.save_user_button)

        fiscalNumberEditText.filters = arrayOf(InputFilter.LengthFilter(9))
        cpEditText.filters = arrayOf(InputFilter.LengthFilter(5))

        CoroutineScope(Dispatchers.IO).launch {
            val user = db.personDao().getUser()
            CoroutineScope(Dispatchers.Main).launch {
                nameEditText.setText(user.name)
                lastNameEditText.setText(user.lastName)
                fiscalNumberEditText.setText(user.fiscalNumber)
                addressEditText.setText(user.address)
                cityEditText.setText(user.city)
                cpEditText.setText(user.cp)
                ibanEditText.setText(user.iban)
            }
        }

        saveButton.setOnClickListener {
            Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
            val name = nameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val fiscalNumber = fiscalNumberEditText.text.toString()
            val address = addressEditText.text.toString()
            val city = cityEditText.text.toString()
            val cpText = cpEditText.text.toString()
            val iban = ibanEditText.text.toString()

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
            Log.d("UserForm", "EditTexts: passed")

            if (utilities.fiscalNumberCheck(fiscalNumber, fiscalNumberEditText)) {
                return@setOnClickListener
            }
            Log.d("UserForm", "FiscalNumber: passed")

            if (utilities.isIbanEmpty(ibanEditText)) {
                return@setOnClickListener
            }
            Log.d("UserForm", "Iban: passed")

            val user = Person(
                name = name,
                lastName = lastName,
                fiscalNumber = fiscalNumber,
                address = address,
                city = city,
                cp = cpText,
                isUser = true,
                iban = iban
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.personDao().deleteUser()
                Log.d("UserForm", "User deleted")
                db.personDao().addPerson(user)
                Log.d("UserForm", "User added")
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