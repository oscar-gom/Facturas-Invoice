package com.example.facturas.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.facturas.MainApplication
import com.example.facturas.R
import com.example.facturas.models.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InvoiceSelectClient : Fragment() {

    private var db = MainApplication.database
    private lateinit var spinnerClients: Spinner
    private lateinit var textViewClientDetails: TextView
    private lateinit var buttonContinue: Button
    private lateinit var clients: List<Person>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_invoice_select_client, container, false)

        spinnerClients = view.findViewById(R.id.spinner_clients)
        textViewClientDetails = view.findViewById(R.id.textview_client_details)
        buttonContinue = view.findViewById(R.id.button_continue)

        loadClients()

        return view
    }

    private fun loadClients() {
        db.personDao().getClients().observe(viewLifecycleOwner) { clientList ->
            clients = listOf(clientList)
            val clientNames = clients.map { "${it.name} ${it.lastName}" }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, clientNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerClients.adapter = adapter

            spinnerClients.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedClient = clients[position]
                    textViewClientDetails.text = """
                        Nombre: ${selectedClient.name} ${selectedClient.lastName}
                        NIF: ${selectedClient.fiscalNumber}
                        Dirección: ${selectedClient.address}, ${selectedClient.city}, ${selectedClient.cp}
                    """.trimIndent()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    textViewClientDetails.text = ""
                }
            }
        }
    }
}