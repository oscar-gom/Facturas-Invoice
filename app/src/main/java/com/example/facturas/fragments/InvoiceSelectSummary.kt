package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import java.math.BigDecimal
import java.math.RoundingMode

class InvoiceSelectSummary : Fragment() {
    private val args: InvoiceSelectSummaryArgs by navArgs()
    private val db = MainApplication.database
    private var subtotal: Double = 0.0
    private var total: Double = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invoice_select_summary, container, false)

        val clientName = view.findViewById<TextView>(R.id.client_name)
        val clientNif = view.findViewById<TextView>(R.id.client_nif)
        val services = view.findViewById<TextView>(R.id.services)
        val subtotalText = view.findViewById<TextView>(R.id.subtotal)
        val totalText = view.findViewById<TextView>(R.id.total)

        getClient(clientName, clientNif)
        getServices(services, subtotalText, totalText)

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun getServices(services: TextView, subtotalText: TextView, totalText: TextView) {
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
                        "${services.text} ${servicesList[i].description}: ${servicesList[i].price}€ x${units[i]} -${discounts[i]}% \n"

                    val serviceSubtotal =
                        BigDecimal((servicesList[i].price * (1 - (discounts[i] / 100.0))) * units[i]).setScale(
                            2, RoundingMode.HALF_EVEN
                        ).toDouble()
                    subtotal += serviceSubtotal
                    Log.d("InvoiceSelectSummary", subtotal.toString())
                    total += BigDecimal(serviceSubtotal * ((servicesList[i].tax / 100.0) + 1)).setScale(
                        2, RoundingMode.HALF_EVEN
                    ).toDouble()
                    Log.d("InvoiceSelectSummary", total.toString())

                    subtotalText.text = "Subtotal: $subtotal€"
                    totalText.text = "Total: $total€"
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