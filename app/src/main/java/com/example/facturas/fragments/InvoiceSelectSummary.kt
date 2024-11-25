package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Service
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoiceSelectSummary : Fragment() {
    private val args: InvoiceSelectSummaryArgs by navArgs()
    private val db = MainApplication.database
    private val prices = mutableListOf<Double>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invoice_select_summary, container, false)

        val clientName = view.findViewById<TextView>(R.id.client_name)
        val clientNif = view.findViewById<TextView>(R.id.client_nif)
        val services = view.findViewById<TextView>(R.id.services)

        getClient(clientName, clientNif)
        getServices(services)

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun getServices(services: TextView) {
        val servicesList = mutableListOf<Service>()
        val units = args.servicesUnits
        val discounts = args.servicesDiscount

        CoroutineScope(Dispatchers.IO).launch {
            val servicesIds = args.servicesId
            for (serviceId in servicesIds) {
                val service = db.serviceDao().getServiceById(serviceId)
                servicesList.add(service)
            }
            withContext(Dispatchers.Main) {
                for (i in servicesList.indices) {
                    services.text =
                        "${services.text} ${servicesList[i].description} - ${servicesList[i].price} x${units[i]} -${discounts[i]}% \n"
                }
            }
        }


    }

    private fun getClient(clientName: TextView, clientNif: TextView) {
        val clientId = args.clientId

        CoroutineScope(Dispatchers.IO).launch {
            val client = db.personDao().getPersonById(clientId)
            withContext(Dispatchers.Main) {
                clientName.text = client.name
                clientNif.text = client.fiscalNumber
            }
        }
    }
}