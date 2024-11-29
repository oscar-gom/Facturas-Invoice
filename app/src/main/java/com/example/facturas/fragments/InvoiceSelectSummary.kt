package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Invoice
import com.example.facturas.models.Person
import com.example.facturas.models.PersonInvoice
import com.example.facturas.models.Service
import com.example.facturas.models.ServiceInvoice
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itextpdf.html2pdf.HtmlConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Year

class InvoiceSelectSummary : Fragment() {
    private val args: InvoiceSelectSummaryArgs by navArgs()
    private val db = MainApplication.database
    private var subtotal: Double = 0.0
    private var total: Double = 0.0
    private val servicesList = mutableListOf<Service>()
    private var invoiceNum: String = ""

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
        val buttonCreate = view.findViewById<Button>(R.id.button_create_invoice)

        getClient(clientName, clientNif)
        getServices(services, subtotalText, totalText)

        buttonCreate.setOnClickListener {
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
                    units = args.servicesUnits[servicesList.indexOf(service)],
                    discount = args.servicesDiscount[servicesList.indexOf(service)]
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
        saveHistory(clientHistory, userHistory, servicesInvoiceHistory)
        templateCreation(client, user, servicesInvoiceHistory)
    }

    private suspend fun templateCreation(
        receiver: Person,
        emitter: Person,
        services: MutableList<ServiceInvoice>
    ) {

        Log.d("InvoiceSelectSummary", "receiver: ${receiver.name}")
        Log.d("InvoiceSelectSummary", "emitter: ${emitter.name}")
        for (service in services) {
            Log.d("InvoiceSelectSummary", "service: ${service.description}")
        }


        val htmlContent = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Factura</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
        }
        .invoice-container {
            width: 80%;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .invoice-header, .invoice-parties {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
        }
        .invoice-header div, .invoice-parties div {
            width: 30%;
        }
        .invoice-header div p, .invoice-parties div p {
            margin: 5px 0;
            white-space: nowrap; /* Evita que el texto pase a la siguiente línea */
        }
        .invoice-header div p span, .invoice-parties div p span {
            font-weight: bold;
        }
        .invoice-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        .invoice-table th, .invoice-table td {
            border: 1px solid #000;
            padding: 8px;
            text-align: left;
        }
        .invoice-table th {
            background-color: #f4f4f4;
        }
        .invoice-summary {
            width: 100%;
            display: flex;
            justify-content: flex-end;
            margin-bottom: 20px;
        }
        .invoice-summary div {
            width: 30%;
        }
        .invoice-summary p {
            margin: 5px 0;
        }
        .invoice-summary p span {
            font-weight: bold;
        }
        .invoice-summary span.total {
            font-size: 1.5em;
            font-weight: bold;
        }
        .invoice-conditions {
            margin-top: 20px;
        }
        .invoice-conditions p {
            margin: 5px 0;
        }
    </style>
</head>
<body>
    <div class="invoice-container">
        <div class="invoice-header">
            <div>
                <p><span>Número de Factura:</span> ${invoiceNum}</p>
                <p><span>Fecha de Emisión:</span> ${LocalDate.now()}</p>
            </div>
        </div>
        <div class="invoice-parties">
            <div>
                <p><span>Emisor:</span></p>
                <p>${emitter.name} ${emitter.lastName}</p>
                <p>${emitter.fiscalNumber}</p>
                <p>${emitter.address}</p>
                <p>${emitter.city}</p>
                <p>${emitter.cp}</p>
            </div>
            <div style="text-align: right;">
                <p><span>Receptor:</span></p>
                <p>${receiver.name} ${receiver.lastName}</p>
                <p>${receiver.fiscalNumber}</p>
                <p>${emitter.city}</p>
                <p>${emitter.cp}</p>
            </div>
        </div>
        <table class="invoice-table">
            <thead>
                <tr>
                    <th>Descripción</th>
                    <th>Unidades</th>
                    <th>Descuento</th>
                    <th>Subtotal</th>
                    <th>IVA</th>
                    <th>Total</th>
                </tr>
            </thead>
            <tbody>
                ${
            services.forEach { service ->
                """
                    <tr>
                        <td>${service.description}</td>
                        <td>${service.units}</td>
                        <td>${service.discount}%</td>
                        <td>${service.subTotal}</td>
                        <td>${service.tax}%</td>
                        <td>${service.total}</td>
                    </tr>
                    """
            }
        }
            </tbody>
        </table>
        <div class="invoice-summary">
            <div>
                <p><span>Subtotal:</span> ${services.sumOf { it.subTotal }}€</p>
                <p><span>Total:</span> <span class="total">${services.sumOf { it.total }}€</span></p>
            </div>
        </div>
        <div class="invoice-conditions">
            <p><span>Forma de Pago:</span></p>
            <p>IBAN: ${emitter.iban}</p>
        </div>
    </div>
</body>
</html>
""".trimIndent()

        val fileName = "factura-${invoiceNum}.pdf"
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        if (file.exists()) {
            file.delete()
        }
        Toast.makeText(context, "Creando la factura...", Toast.LENGTH_SHORT).show()
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(file)
        }
        HtmlConverter.convertToPdf(htmlContent, outputStream)
        withContext(Dispatchers.IO) {
            outputStream.close()
        }
        Toast.makeText(context, "¡Factura creada con exito!", Toast.LENGTH_SHORT).show()
    }

    private suspend fun saveHistory(
        receiver: PersonInvoice,
        emitter: PersonInvoice,
        services: MutableList<ServiceInvoice>
    ) {
        val num: Int = CoroutineScope(Dispatchers.IO).async {
            db.invoiceDao().getLastInvoiceId() + 1
        }.await()
        invoiceNum = "${Year.now().value}-$num"
        val invoice = Invoice(
            invoiceNum = invoiceNum,
            client = receiver,
            user = emitter,
            services = services,
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.personInvoiceDao().addPersonInvoice(receiver)
            db.personInvoiceDao().addPersonInvoice(emitter)
            services.forEach() { service ->
                db.serviceInvoiceDao().addServiceInvoice(service)
            }
            db.invoiceDao().addInvoice(invoice)
        }
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
                        "${services.text} ${servicesList[i].description}: ${servicesList[i].price}€ x${args.servicesUnits[i]} -${args.servicesDiscount[i]}% \n"

                    val serviceSubtotal =
                        BigDecimal((servicesList[i].price * (1 - (args.servicesDiscount[i] / 100.0))) * args.servicesUnits[i]).setScale(
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