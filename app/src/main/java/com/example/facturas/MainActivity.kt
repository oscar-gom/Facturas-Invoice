package com.example.facturas

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.facturas.models.Invoice
import com.example.facturas.models.Person
import com.example.facturas.models.Service
import com.itextpdf.html2pdf.HtmlConverter
import java.io.File
import java.io.FileOutputStream
import java.time.Year

class MainActivity : AppCompatActivity() {

    private lateinit var servicesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        servicesContainer =
            findViewById(R.id.services_container)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.emit_button)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.add_service_button).setOnClickListener {
            addServiceView()
        }

        findViewById<Button>(R.id.emit_button).setOnClickListener {
            emitInvoice()
        }
    }

    private fun addServiceView() {
        val serviceView = layoutInflater.inflate(R.layout.service_item, servicesContainer, false)
        servicesContainer.addView(serviceView)
    }

    private fun emitInvoice() {
        val invoiceNumber = findViewById<EditText>(R.id.invoice_number).text.toString()
        val invoiceDate = findViewById<EditText>(R.id.invoice_date).text.toString()
        val invoiceExpiration = findViewById<EditText>(R.id.invoice_expiration).text.toString()

        val emitter = Person(
            findViewById<EditText>(R.id.emitter_name).text.toString(),
            findViewById<EditText>(R.id.emitter_last_name).text.toString(),
            findViewById<EditText>(R.id.emitter_fiscal_number).text.toString(),
            findViewById<EditText>(R.id.emitter_address).text.toString(),
            findViewById<EditText>(R.id.emitter_city_cp).text.toString()
        )

        val receiver = Person(
            findViewById<EditText>(R.id.receiver_name).text.toString(),
            findViewById<EditText>(R.id.receiver_last_name).text.toString(),
            findViewById<EditText>(R.id.receiver_fiscal_number).text.toString(),
            findViewById<EditText>(R.id.receiver_address).text.toString(),
            findViewById<EditText>(R.id.receiver_city_cp).text.toString()
        )

        val services = mutableListOf<Service>()
        for (i in 0 until servicesContainer.childCount) {
            val serviceView = servicesContainer.getChildAt(i)
            val name = serviceView.findViewById<EditText>(R.id.service_name).text.toString()
            val price = serviceView.findViewById<EditText>(R.id.service_price).text.toString().toDouble()
            val discount = serviceView.findViewById<EditText>(R.id.service_discount).text.toString().toDouble()
            val units = serviceView.findViewById<EditText>(R.id.service_units).text.toString().toInt()
            val tax = serviceView.findViewById<EditText>(R.id.service_tax).text.toString().toDouble()

            services.add(Service(name, price, discount, units, tax))
        }

        val iban = findViewById<EditText>(R.id.emitter_iban).text.toString()

        val invoice = Invoice(
            id = invoiceNumber,
            date = invoiceDate,
            expiration = invoiceExpiration,
            services = services,
            iban = iban
        )

        templateCreation(invoice, emitter, receiver)

        Toast.makeText(this, "Factura emitida", Toast.LENGTH_SHORT).show()
    }

    private fun templateCreation(invoice: Invoice, emitter: Person, receiver: Person) {

        val numFactura = "${Year.now().value}-${invoice.id}"
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
                <p><span>Número de Factura:</span> ${numFactura}</p>
                <p><span>Fecha de Emisión:</span> ${invoice.date}</p>
                <p><span>Fecha de Vencimiento:</span> ${invoice.expiration}</p>
            </div>
        </div>
        <div class="invoice-parties">
            <div>
                <p><span>Emisor:</span></p>
                <p>${emitter.name} ${emitter.lastName}</p>
                <p>${emitter.fiscalNumber}</p>
                <p>${emitter.address}</p>
                <p>${emitter.cityCP}</p>
            </div>
            <div style="text-align: right;">
                <p><span>Receptor:</span></p>
                <p>${receiver.name} ${receiver.lastName}</p>
                <p>${receiver.fiscalNumber}</p>
                <p>${receiver.address}</p>
                <p>${receiver.cityCP}</p>
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
            invoice.services.joinToString("") { service ->
                """
                    <tr>
                        <td>${service.name}</td>
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
                <p><span>Subtotal:</span> ${invoice.services.sumOf { it.subTotal }}€</p>
                <p><span>Total:</span> <span class="total">${invoice.services.sumOf { it.total }}€</span></p>
            </div>
        </div>
        <div class="invoice-conditions">
            <p><span>Forma de Pago:</span></p>
            <p>IBAN: ${invoice.iban}</p>
        </div>
    </div>
</body>
</html>
""".trimIndent()

        val fileName = "factura-${numFactura}.pdf"
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        val outputStream = FileOutputStream(file)
        HtmlConverter.convertToPdf(htmlContent, outputStream)
        outputStream.close()
    }
}