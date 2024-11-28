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
import com.example.facturas.models.Person
import com.example.facturas.models.PersonInvoice
import com.example.facturas.models.Service
import com.example.facturas.models.ServiceInvoice
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Year

class InvoiceSelectSummary : Fragment() {
    private val args: InvoiceSelectSummaryArgs by navArgs()
    private val db = MainApplication.database
    private var subtotal: Double = 0.0
    private var total: Double = 0.0
    private val servicesList = mutableListOf<Service>()
    private val units = args.servicesUnits
    private val discounts = args.servicesDiscount

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
        val button_create = view.findViewById<TextView>(R.id.button_create_invoice)

        getClient(clientName, clientNif)
        getServices(services, subtotalText, totalText)

        button_create.setOnClickListener {
            showConfirmationDialog()
        }

        return view
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("¿Seguro que quieres crear esta factura?")
            .setPositiveButton("Sí") { dialog, _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    createInvoice()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private suspend fun createInvoice() {
        // Create history data
        val servicesInvoiceHistory = mutableListOf<ServiceInvoice>()
        servicesList.forEach() { service ->
            servicesInvoiceHistory.add(
                ServiceInvoice(
                    serviceInvoiceId = service.serviceId,
                    description = service.description,
                    price = service.price,
                    tax = service.tax,
                    units = units[servicesList.indexOf(service)],
                    discount = discounts[servicesList.indexOf(service)]
                )
            )
        }
        val client: Person = CoroutineScope(Dispatchers.IO).async {
            db.personDao().getPersonById(args.clientId)
        }.await()
        val clientHistory = PersonInvoice(
            personInvoiceId = client.personId,
            name = client.name,
            lastName = client.lastName,
            fiscalNumber = client.fiscalNumber,
            address = client.address,
            city = client.city,
            cp = client.cp,
            isUser = false
        )
        val user: Person = CoroutineScope(Dispatchers.IO).async {
            db.personDao().getUser()
        }.await()
        val userHistory = PersonInvoice(
            personInvoiceId = user.personId,
            name = user.name,
            lastName = user.lastName,
            fiscalNumber = user.fiscalNumber,
            address = user.address,
            city = user.city,
            cp = user.cp,
            isUser = true
        )
        templateCreation(clientHistory, userHistory, servicesInvoiceHistory)
    }

    private suspend fun templateCreation(
        receiver: PersonInvoice,
        emitter: PersonInvoice,
        services: MutableList<ServiceInvoice>
    ) {
        val num: Int = CoroutineScope(Dispatchers.IO).async {
            db.invoiceDao().getLastInvoiceId() + 1
        }.await()
        val invoiceNum = "${Year.now().value}-$num"
    }

    @SuppressLint("SetTextI18n")
    private fun getServices(services: TextView, subtotalText: TextView, totalText: TextView) {
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