package com.example.facturas.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServiceForm : Fragment() {
    val db = MainApplication.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_form, container, false)

        val descriptionEditText = view.findViewById<EditText>(R.id.service_description)
        val priceEditText = view.findViewById<EditText>(R.id.service_price)
        val discountEditText = view.findViewById<EditText>(R.id.service_discount)
        val unitsEditText = view.findViewById<EditText>(R.id.service_units)
        val taxSpinner = view.findViewById<Spinner>(R.id.service_tax)
        val saveButton = view.findViewById<Button>(R.id.save_service_button)

        // Spinner
        val taxOptions = arrayOf("0%", "4%", "5%", "10%", "21%")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, taxOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taxSpinner.adapter = adapter

        saveButton.setOnClickListener {
            val description = descriptionEditText.text.toString()
            val priceText = priceEditText.text.toString()
            val discountText = discountEditText.text.toString()
            val unitsText = unitsEditText.text.toString()
            val taxText = taxSpinner.selectedItem.toString().replace("%", "")

            // Check empty spaces
            if (description.isEmpty()) {
                descriptionEditText.error = "La descripción es obligatoria"
                return@setOnClickListener
            }
            if (priceText.isEmpty()) {
                priceEditText.error = "El precio es obligatorio"
                return@setOnClickListener
            }
            if (discountText.isEmpty()) {
                discountEditText.error = "El descuento es obligatorio"
                return@setOnClickListener
            }
            if (unitsText.isEmpty()) {
                unitsEditText.error = "Las unidades son obligatorias"
                return@setOnClickListener
            }

            val price = priceText.toDouble()
            val discount = discountText.toDouble()
            val units = unitsText.toInt()
            val tax = taxText.toDouble()

            // Other validations
            if (price <= 0) {
                priceEditText.error = "El precio debe ser mayor a 0"
                return@setOnClickListener
            }
            if (discount < 0) {
                discountEditText.error = "El descuento no puede ser negativo"
                return@setOnClickListener
            }
            if (units <= 0) {
                unitsEditText.error = "Las unidades deben ser mayor a 0"
                return@setOnClickListener
            }

            val service = Service(
                description = description,
                price = price,
                discount = discount,
                units = units,
                tax = tax
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.serviceDao().addService(service)
                CoroutineScope(Dispatchers.Main).launch {
                    descriptionEditText.text.clear()
                    priceEditText.text.clear()
                    discountEditText.text.clear()
                    unitsEditText.text.clear()
                    taxSpinner.setSelection(0)
                    Toast.makeText(
                        requireContext(),
                        "¡Exito! Servicio creado", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return view
    }
}