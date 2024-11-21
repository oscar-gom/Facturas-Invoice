package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.facturas.MainApplication
import com.example.facturas.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoiceSelectServices : Fragment() {
    private val args: InvoiceSelectServicesArgs by navArgs()
    val db = MainApplication.database

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invoice_select_services, container, false)

        val servicesContainer: LinearLayout = view.findViewById(R.id.services_container)
        val buttonAddService: Button = view.findViewById(R.id.button_add_service)
        val buttonContinue: Button = view.findViewById(R.id.button_continue)

        buttonAddService.setOnClickListener {
            addServiceView(servicesContainer)
        }

        buttonContinue.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                saveServiceDataAndContinue(servicesContainer)
            }
        }

        addServiceView(servicesContainer)

        return view
    }

    @SuppressLint("MissingInflatedId")
    private fun addServiceView(container: LinearLayout) {
        val serviceView = layoutInflater.inflate(R.layout.service_item, container, false)
        val spinnerServices: Spinner = serviceView.findViewById(R.id.spinner_services)
        val editTextUnits: EditText = serviceView.findViewById(R.id.edittext_units)
        val editTextDiscount: EditText = serviceView.findViewById(R.id.edittext_discount)
        val buttonRemoveService: Button = serviceView.findViewById(R.id.button_remove_service)

        loadServices(spinnerServices)

        editTextUnits.setText("1")
        editTextDiscount.setText("0")

        buttonRemoveService.setOnClickListener {
            container.removeView(serviceView)
        }

        container.addView(serviceView)
    }

    private fun loadServices(spinnerServices: Spinner) {
        db.serviceDao().getAllServices().observe(viewLifecycleOwner) { services ->
            val serviceDescriptions = services.map { it.description }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                serviceDescriptions
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerServices.adapter = adapter
        }
    }

    private suspend fun saveServiceDataAndContinue(container: LinearLayout) {
        val serviceIds = mutableListOf<Int>()
        val units = mutableListOf<Int>()
        val discounts = mutableListOf<Float>()

        for (i in 0 until container.childCount) {
            val serviceView = container.getChildAt(i)
            val spinnerServices: Spinner = serviceView.findViewById(R.id.spinner_services)
            val editTextUnits: EditText = serviceView.findViewById(R.id.edittext_units)
            val editTextDiscount: EditText = serviceView.findViewById(R.id.edittext_discount)

            val selectedService = spinnerServices.selectedItem.toString()
            val serviceId = withContext(Dispatchers.IO) {
                db.serviceDao().getServiceIdByDescription(selectedService)
            }
            val unit = editTextUnits.text.toString().toInt()
            val discount = editTextDiscount.text.toString().toFloat()

            serviceIds.add(serviceId)
            units.add(unit)
            discounts.add(discount)
        }

        for (service in serviceIds) {
            Log.d("InvoiceSelectServices", "Service ID: $service")
        }

        for (unit in units) {
            Log.d("InvoiceSelectServices", "Unit: $unit")
        }

        for (discount in discounts) {
            Log.d("InvoiceSelectServices", "Discount: $discount")
        }

        val action = InvoiceSelectServicesDirections.actionInvoiceSelectServicesToInvoiceSelectSummary(
            clientId = args.clientId,
            servicesId = serviceIds.toIntArray(),
            servicesUnits = units.toIntArray(),
            servicesDiscount = discounts.toFloatArray(),
        )
        findNavController().navigate(action)
    }
}